import numpy
import time
import struct
from PIL import Image
from MeshBuilder import MeshBuilder
from Vector import Vector
from CuraSceneNode import CuraSceneNode as SceneNode

def generateSceneNode(file_name, xz_size, peak_height, base_height, blur_iterations, max_size,lighter_is_higher):
    scene_node = SceneNode()

    mesh = MeshBuilder()

  #  img = QImage(file_name)
    im= Image.open(file_name)
#    if im.isNull():
       # Logger.log("e", "Image is corrupt.")
 #       return None

  #  width = max(img.width(), 2)
  #  height = max(img.height(), 2)
    width = max(im.size[0], 2)
    height = max(im.size[1], 2)
    aspect = height / width

#    if im.width() < 2 or im.height() < 2:
       # img = img.scaled(width, height, Qt.IgnoreAspectRatio)
 #       im = im.resize(width, height, Image.ANTIALIAS)
    base_height = max(base_height, 0)
    peak_height = max(peak_height, -base_height)

    xz_size = max(xz_size, blur_iterations)
    scale_vector = Vector(xz_size, peak_height, xz_size)

    if width > height:
        scale_vector = scale_vector.set(z=scale_vector.z * aspect)
    elif height > width:
        scale_vector = scale_vector.set(x=scale_vector.x / aspect)

    if width > max_size or height > max_size:
        scale_factor = max_size / width
        if height > width:
            scale_factor = max_size / height

        width = int(max(round(width * scale_factor), 2))
        height = int(max(round(height * scale_factor), 2))
       # img = img.scaled(width, height, Qt.IgnoreAspectRatio)
        im = im.resize((width, height), Image.ANTIALIAS)
    width_minus_one = width - 1
    height_minus_one = height - 1

    #Job.yieldThread()

    texel_width = 1.0 / (width_minus_one) * scale_vector.x
    texel_height = 1.0 / (height_minus_one) * scale_vector.z

    height_data = numpy.zeros((height, width), dtype=numpy.float32)

    for x in range(0, width):
        for y in range(0, height):
           # qrgb = img.pixel(x, y)
            qrgb = im.getpixel((x, y))
            R=qrgb[0]
            G=qrgb[1]
            B=qrgb[2]
            avg=float(R+G+B)/(3*255)
           # qR=qRed(qrgb)
           # qG=qGreen(qrgb)
           # qB=qBlue(qrgb)
           # avg=float(qR+qG+qB)/(3 * 255)
           # avg = float(qRed(qrgb) + qGreen(qrgb) + qBlue(qrgb)) / (3 * 255)
            height_data[y, x] = avg

    #Job.yieldThread()

    if not lighter_is_higher:
        height_data = 1 - height_data

    for _ in range(0,blur_iterations):
        copy = numpy.pad(height_data, ((1, 1), (1, 1)), mode="edge")

        height_data += copy[1:-1, 2:]
        height_data += copy[1:-1, :-2]
        height_data += copy[2:, 1:-1]
        height_data += copy[:-2, 1:-1]

        height_data += copy[2:, 2:]
        height_data += copy[:-2, 2:]
        height_data += copy[2:, :-2]
        height_data += copy[:-2, :-2]

        height_data /= 9

      #  Job.yieldThread()

    height_data *= scale_vector.y
    height_data += base_height

    heightmap_face_count = 2 * height_minus_one * width_minus_one
    total_face_count = heightmap_face_count + (width_minus_one * 2) * (height_minus_one * 2) + 2

    mesh.reserveFaceCount(total_face_count)

    # initialize to texel space vertex offsets.
    # 6 is for 6 vertices for each texel quad.
    heightmap_vertices = numpy.zeros((width_minus_one * height_minus_one, 6, 3), dtype=numpy.float32)
    heightmap_vertices = heightmap_vertices + numpy.array([[
        [0, base_height, 0],
        [0, base_height, texel_height],
        [texel_width, base_height, texel_height],
        [texel_width, base_height, texel_height],
        [texel_width, base_height, 0],
        [0, base_height, 0]
    ]], dtype=numpy.float32)

    offsetsz, offsetsx = numpy.mgrid[0: height_minus_one, 0: width - 1]
    offsetsx = numpy.array(offsetsx, numpy.float32).reshape(-1, 1) * texel_width
    offsetsz = numpy.array(offsetsz, numpy.float32).reshape(-1, 1) * texel_height

    # offsets for each texel quad
    heightmap_vertex_offsets = numpy.concatenate(
        [offsetsx, numpy.zeros((offsetsx.shape[0], offsetsx.shape[1]), dtype=numpy.float32), offsetsz], 1)
    heightmap_vertices += heightmap_vertex_offsets.repeat(6, 0).reshape(-1, 6, 3)

    # apply height data to y values
    heightmap_vertices[:, 0, 1] = heightmap_vertices[:, 5, 1] = height_data[:-1, :-1].reshape(-1)
    heightmap_vertices[:, 1, 1] = height_data[1:, :-1].reshape(-1)
    heightmap_vertices[:, 2, 1] = heightmap_vertices[:, 3, 1] = height_data[1:, 1:].reshape(-1)
    heightmap_vertices[:, 4, 1] = height_data[:-1, 1:].reshape(-1)

    heightmap_indices = numpy.array(numpy.mgrid[0:heightmap_face_count * 3], dtype=numpy.int32).reshape(-1, 3)

    mesh._vertices[0:(heightmap_vertices.size // 3), :] = heightmap_vertices.reshape(-1, 3)
    mesh._indices[0:(heightmap_indices.size // 3), :] = heightmap_indices

    mesh._vertex_count = heightmap_vertices.size // 3
    mesh._face_count = heightmap_indices.size // 3

    geo_width = width_minus_one * texel_width
    geo_height = height_minus_one * texel_height

    # bottom
    mesh.addFaceByPoints(0, 0, 0, 0, 0, geo_height, geo_width, 0, geo_height)
    mesh.addFaceByPoints(geo_width, 0, geo_height, geo_width, 0, 0, 0, 0, 0)

    # north and south walls
    for n in range(0, width_minus_one):
        x = n * texel_width
        nx = (n + 1) * texel_width

        hn0 = height_data[0, n]
        hn1 = height_data[0, n + 1]

        hs0 = height_data[height_minus_one, n]
        hs1 = height_data[height_minus_one, n + 1]

        mesh.addFaceByPoints(x, 0, 0, nx, 0, 0, nx, hn1, 0)
        mesh.addFaceByPoints(nx, hn1, 0, x, hn0, 0, x, 0, 0)

        mesh.addFaceByPoints(x, 0, geo_height, nx, 0, geo_height, nx, hs1, geo_height)
        mesh.addFaceByPoints(nx, hs1, geo_height, x, hs0, geo_height, x, 0, geo_height)

    # west and east walls
    for n in range(0, height_minus_one):
        y = n * texel_height
        ny = (n + 1) * texel_height

        hw0 = height_data[n, 0]
        hw1 = height_data[n + 1, 0]

        he0 = height_data[n, width_minus_one]
        he1 = height_data[n + 1, width_minus_one]

        mesh.addFaceByPoints(0, 0, y, 0, 0, ny, 0, hw1, ny)
        mesh.addFaceByPoints(0, hw1, ny, 0, hw0, y, 0, 0, y)

        mesh.addFaceByPoints(geo_width, 0, y, geo_width, 0, ny, geo_width, he1, ny)
        mesh.addFaceByPoints(geo_width, he1, ny, geo_width, he0, y, geo_width, 0, y)

    mesh.calculateNormals(fast=True)

    scene_node.setMeshData(mesh.build())
    saveScene(file, scene_node)
   # return scene_node

def saveScene(filename, object):
    f = open(filename, 'wb')
    _writeBinary(f, object)
    f.close()

def _writeBinary(stream, node):
    stream.write("Uranium STLWriter {0}".format(time.strftime("%a %d %b %Y %H:%M:%S")).encode().ljust(80, b"\000"))

    face_count = 0
#    nodes = list(node)
 #   for node in nodes:
    if node.getMeshData().hasIndices():
        face_count += node.getMeshData().getFaceCount()
    else:
        face_count += node.getMeshData().getVertexCount() / 3

    stream.write(struct.pack("<I", int(face_count)))  # Write number of faces to STL

#    for node in node:
    mesh_data = node.getMeshData().getTransformed(node.getWorldTransformation())

    if mesh_data.hasIndices():
        verts = mesh_data.getVertices()
        for face in mesh_data.getIndices():
            v1 = verts[face[0]]
            v2 = verts[face[1]]
            v3 = verts[face[2]]
            stream.write(struct.pack("<fff", 0.0, 0.0, 0.0))
            stream.write(struct.pack("<fff", v1[0], -v1[2], v1[1]))
            stream.write(struct.pack("<fff", v2[0], -v2[2], v2[1]))
            stream.write(struct.pack("<fff", v3[0], -v3[2], v3[1]))
            stream.write(struct.pack("<H", 0))
    else:
        num_verts = mesh_data.getVertexCount()
        verts = mesh_data.getVertices()
        for index in range(0, num_verts - 1, 3):
            v1 = verts[index]
            v2 = verts[index + 1]
            v3 = verts[index + 2]
            stream.write(struct.pack("<fff", 0.0, 0.0, 0.0))
            stream.write(struct.pack("<fff", v1[0], -v1[2], v1[1]))
            stream.write(struct.pack("<fff", v2[0], -v2[2], v2[1]))
            stream.write(struct.pack("<fff", v3[0], -v3[2], v3[1]))
            stream.write(struct.pack("<H", 0))


file = "/sdcard/Android/data/com.android.browser/files/yushengnan4.stl"
#xz_size = 120.0
#peak_height = 20
#base_height = 0.4
#blur_iterations= 1
#max_size = 512
#lighter_is_higher = False

#object = generateSceneNode(file_name, xz_size, peak_height, base_height, blur_iterations, max_size, lighter_is_higher,file)
#generateSceneNode(file_name, 120, 20, 0.4, 1, 512, False)
#file = 'F:\\111\\img5.stl'
#saveScene(file,object)
