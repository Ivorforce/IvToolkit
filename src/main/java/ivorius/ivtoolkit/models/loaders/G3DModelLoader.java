/*
 * Notice: This is a modified version of a libgdx file. See https://github.com/libgdx/libgdx for the original work.
 *
 * Copyright 2011 See libgdx AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ivorius.ivtoolkit.models.loaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ivorius.ivtoolkit.models.*;
import ivorius.ivtoolkit.models.attributes.BlendingAttribute;
import ivorius.ivtoolkit.models.attributes.ColorAttribute;
import ivorius.ivtoolkit.models.attributes.FloatAttribute;
import ivorius.ivtoolkit.models.attributes.TextureAttribute;
import ivorius.ivtoolkit.models.data.VertexAttribute;
import ivorius.ivtoolkit.models.data.VertexAttributes;
import ivorius.ivtoolkit.models.loaders.data.ColorDeserializer;
import ivorius.ivtoolkit.models.loaders.data.QuaternionDeserializer;
import ivorius.ivtoolkit.models.loaders.data.Vector2fDeserializer;
import ivorius.ivtoolkit.models.loaders.data.Vector3fDeserializer;
import ivorius.ivtoolkit.models.loaders.g3d.*;
import ivorius.ivtoolkit.models.textures.Texture;
import ivorius.ivtoolkit.models.textures.TextureProvider;
import ivorius.ivtoolkit.models.textures.TextureSub;
import ivorius.ivtoolkit.models.utils.ArrayMap;
import ivorius.ivtoolkit.models.utils.BufferUtils;
import ivorius.ivtoolkit.models.utils.MatrixMathUtils;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.io.Reader;
import java.util.*;
import java.util.List;

/**
 * Created by lukas on 21.09.14.
 */
public class G3DModelLoader implements ModelLoader
{
    public static final short VERSION_HI = 0;
    public static final short VERSION_LO = 1;

    private Logger logger;
    private Gson gson;

    public G3DModelLoader(Logger logger)
    {
        this.logger = logger;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Color.class, new ColorDeserializer());
        gsonBuilder.registerTypeAdapter(Quaternion.class, new QuaternionDeserializer());
        gsonBuilder.registerTypeAdapter(Vector2f.class, new Vector2fDeserializer());
        gsonBuilder.registerTypeAdapter(Vector3f.class, new Vector3fDeserializer());
        gson = gsonBuilder.create();
    }

    @Override
    public Model createModel(Reader reader, TextureProvider textureProvider)
    {
        G3DModel g3DModel = gson.fromJson(reader, G3DModel.class);

        return modelFromRawModel(g3DModel, textureProvider, logger);
    }

    public static Model modelFromRawModel(G3DModel g3DModel, TextureProvider textureProvider, Logger logger)
    {
        if (g3DModel.version == null)
            g3DModel.version = new short[]{VERSION_HI, VERSION_LO};

        if (g3DModel.version[0] != VERSION_HI || g3DModel.version[1] != VERSION_LO)
        {
            logger.error("Version number of g3d file unknown");
            return null;
        }

        Map<String, Node> nodeMap = new HashMap<>();

        Model model = new Model();
        loadMeshes(model, Arrays.asList(g3DModel.meshes));
        loadMaterials(model, Arrays.asList(g3DModel.materials), textureProvider, logger);
        loadNodes(model, Arrays.asList(g3DModel.nodes), nodeMap);
        loadAnimations(model, Arrays.asList(g3DModel.animations), nodeMap);
        return model;
    }

    private static void loadMeshes(Model model, Iterable<G3DMesh> meshes)
    {
        for (G3DMesh g3DMesh : meshes)
        {
            Mesh mesh = convertMesh(g3DMesh, model.meshParts);

            model.meshes.add(mesh);
            model.disposables.add(mesh);
        }
    }

    public static Mesh convertMesh(G3DMesh g3DMesh, List<MeshPart> meshParts)
    {
        int numIndices = 0;
        for (G3DMeshPart part : g3DMesh.parts)
        {
            numIndices += part.indices.length;
        }
        VertexAttributes attributes = new VertexAttributes(parseVertexAttributes(g3DMesh.attributes));
        int numVertices = g3DMesh.vertices.length / (attributes.vertexSize / 4);

        Mesh mesh = new Mesh(true, numVertices, numIndices, attributes);

        BufferUtils.copy(g3DMesh.vertices, mesh.getVerticesBuffer(), g3DMesh.vertices.length, 0);
        int offset = 0;
        mesh.getIndicesBuffer().clear();
        for (G3DMeshPart part : g3DMesh.parts)
        {
            MeshPart meshPart = new MeshPart();
            meshPart.id = part.id;
            meshPart.primitiveType = parseGLDrawMode(part.type);
            meshPart.indexOffset = offset;
            meshPart.numVertices = part.indices.length;
            meshPart.mesh = mesh;
            mesh.getIndicesBuffer().put(part.indices);
            offset += meshPart.numVertices;
            meshParts.add(meshPart);
        }
        mesh.getIndicesBuffer().position(0);
        return mesh;
    }

    private static void loadNodes(Model model, List<G3DNode> g3DNodes, Map<String, Node> nodeMap)
    {
        Map<String, Material> materials = new HashMap<>();
        for (Material material : model.materials)
            materials.put(material.id, material);

        Map<String, MeshPart> meshParts = new HashMap<>();
        for (MeshPart meshPart : model.meshParts)
            meshParts.put(meshPart.id, meshPart);

        for (G3DNode g3DNode : g3DNodes)
            model.nodes.add(convertNodeWithoutParts(g3DNode, nodeMap));

        for (int i = 0; i < g3DNodes.size(); i++)
            convertNodeParts(model.nodes.get(i), g3DNodes.get(i), nodeMap, materials, meshParts);
    }

    public static Node convertNodeWithoutParts(G3DNode g3DNode, Map<String, Node> nodeMap)
    {
        Node node = new Node();
        node.id = g3DNode.id;
        nodeMap.put(node.id, node);

        if (g3DNode.translation != null)
            node.translation.set(g3DNode.translation);

        if (g3DNode.rotation != null)
            node.rotation.set(g3DNode.rotation);

        if (g3DNode.scale != null)
            node.scale.set(g3DNode.scale);

        if (g3DNode.children != null)
            for (G3DNode g3dChild : g3DNode.children)
            {
                Node child = convertNodeWithoutParts(g3dChild, nodeMap);
                node.children.add(child);
                child.parent = node;
            }

        return node;
    }

    public static void convertNodeParts(Node node, G3DNode g3DNode, Map<String, Node> nodes, Map<String, Material> materials, Map<String, MeshPart> meshParts)
    {
        if (g3DNode.parts != null)
            for (G3DNodePart g3DNodePart : g3DNode.parts)
                node.parts.add(convertNodePart(g3DNodePart, nodes, materials, meshParts));
    }

    public static NodePart convertNodePart(G3DNodePart g3DNodePart, Map<String, Node> nodes, Map<String, Material> materials, Map<String, MeshPart> meshParts)
    {
        NodePart nodePart = new NodePart();

        nodePart.meshPart = meshParts.get(g3DNodePart.meshpartid);

        nodePart.material = materials.get(g3DNodePart.materialid);
        if (nodePart.material == null)
            nodePart.material = new Material();

        nodePart.bones = new Matrix4f[g3DNodePart.bones.length];
        nodePart.invBoneBindTransforms = new ArrayMap<>(Node.class, Matrix4f.class);
        for (int i = 0; i < g3DNodePart.bones.length; i++)
        {
            G3DBone bone = g3DNodePart.bones[i];

            nodePart.bones[i] = new Matrix4f();

            Matrix4f matrix = new Matrix4f();
            MatrixMathUtils.setTRS(matrix, bone.translation, bone.rotation, bone.scale);
            Matrix4f.invert(matrix, matrix);
            nodePart.invBoneBindTransforms.put(nodes.get(bone.node), matrix, i);
        }

        return nodePart;
    }

    private static void loadMaterials(Model model, List<G3DMaterial> g3DMaterials, TextureProvider textureProvider, Logger logger)
    {
        for (G3DMaterial g3DMaterial : g3DMaterials)
            model.materials.add(convertMaterial(g3DMaterial, textureProvider, logger));
    }

    public static Material convertMaterial(G3DMaterial g3DMaterial, TextureProvider textureProvider, Logger logger)
    {
        Material material = new Material();

        material.id = g3DMaterial.id;

        if (g3DMaterial.ambient != null)
            material.set(new ColorAttribute(ColorAttribute.Ambient, g3DMaterial.ambient));
        if (g3DMaterial.diffuse != null)
            material.set(new ColorAttribute(ColorAttribute.Diffuse, g3DMaterial.diffuse));
        if (g3DMaterial.specular != null)
            material.set(new ColorAttribute(ColorAttribute.Specular, g3DMaterial.specular));
        if (g3DMaterial.emissive != null)
            material.set(new ColorAttribute(ColorAttribute.Emissive, g3DMaterial.emissive));
        if (g3DMaterial.reflection != null)
            material.set(new ColorAttribute(ColorAttribute.Reflection, g3DMaterial.reflection));
        if (g3DMaterial.shininess > 0f)
            material.set(new FloatAttribute(FloatAttribute.Shininess, g3DMaterial.shininess));
        if (g3DMaterial.opacity != 1.f)
            material.set(new BlendingAttribute(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, g3DMaterial.opacity));

        if (g3DMaterial.textures != null)
        {
            for (G3DTexture tex : g3DMaterial.textures)
            {
                Texture texture = textureProvider.provideTexture(tex.filename);

                if ((tex.uvScaling != null && (tex.uvScaling.getX() != 1.0f || tex.uvScaling.getY() != 1.0f))
                        || (tex.uvTranslation != null && (tex.uvTranslation.getX() != 0.0f || tex.uvTranslation.getY() != 0.0f)))
                {
                    Vector2f scale = tex.uvScaling != null ? tex.uvScaling : new Vector2f(1, 1);
                    Vector2f trans = tex.uvTranslation != null ? tex.uvTranslation : new Vector2f(0, 0);

                    texture = new TextureSub(texture, trans.getX(), trans.getY(), trans.getX() + scale.getX(), trans.getY() + scale.getY());
                }

                switch (tex.type)
                {
                    case "DIFFUSE":
                        material.set(new TextureAttribute(TextureAttribute.Diffuse, texture));
                        break;
                    case "SPECULAR":
                        material.set(new TextureAttribute(TextureAttribute.Specular, texture));
                        break;
                    case "BUMP":
                        material.set(new TextureAttribute(TextureAttribute.Bump, texture));
                        break;
                    case "NORMAL":
                        material.set(new TextureAttribute(TextureAttribute.Normal, texture));
                        break;
                    default:
                        if (logger != null)
                            logger.warn("Unknown texture type: '" + tex.type + "'");
                }
            }
        }

        return material;
    }

    private static void loadAnimations(Model model, List<G3DAnimation> animations, Map<String, Node> nodes)
    {
        for (G3DAnimation animation : animations)
            model.animations.add(convertAnimation(animation, nodes));
    }

    public static Animation convertAnimation(G3DAnimation g3DAnimation, Map<String, Node> nodes)
    {
        Animation animation = new Animation();

        animation.id = g3DAnimation.id;

        for (G3DNodeAnimation nodeAnimation : g3DAnimation.bones)
            animation.nodeAnimations.add(convertNodeAnimation(nodeAnimation, nodes));

        float highestKeytime = 0.0f;
        for (NodeAnimation nodeAnimation : animation.nodeAnimations)
        {
            for (NodeKeyframe nodeKeyframe : nodeAnimation.keyframes)
            {
                if (nodeKeyframe.keytime > highestKeytime)
                    highestKeytime = nodeKeyframe.keytime;
            }
        }
        animation.duration = highestKeytime;

        return animation;
    }

    public static NodeAnimation convertNodeAnimation(G3DNodeAnimation g3DNodeAnimation, Map<String, Node> nodes)
    {
        NodeAnimation nodeAnimation = new NodeAnimation();

        nodeAnimation.node = nodes.get(g3DNodeAnimation.boneId);

        for (G3DKeyframe keyframe : g3DNodeAnimation.keyframes)
            nodeAnimation.addKeyframe(convertKeyframe(keyframe));

        return nodeAnimation;
    }

    public static NodeKeyframe convertKeyframe(G3DKeyframe g3DKeyframe)
    {
        NodeKeyframe keyframe = new NodeKeyframe();

        keyframe.keytime = g3DKeyframe.keytime;
        keyframe.rotation = g3DKeyframe.rotation;
        keyframe.translation = g3DKeyframe.translation;
        keyframe.scale = g3DKeyframe.scale;

        return keyframe;
    }

    private static int parseGLDrawMode(String type)
    {
        switch (type)
        {
            case "TRIANGLES":
                return GL11.GL_TRIANGLES;
            case "LINES":
                return GL11.GL_LINES;
            case "POINTS":
                return GL11.GL_POINTS;
            case "TRIANGLE_STRIP":
                return GL11.GL_TRIANGLE_STRIP;
            case "LINE_STRIP":
                return GL11.GL_LINE_STRIP;
            default:
                throw new RuntimeException("Unknown primitive type '" + type
                        + "', should be one of triangle, trianglestrip, line, linestrip, lineloop or point");
        }
    }

    public static VertexAttribute[] parseVertexAttributes(String[] attributes)
    {
        List<VertexAttribute> vertexAttributes = new ArrayList<>();
        int unit = 0;
        int blendWeightCount = 0;
        for (String attribute : attributes)
        {
            if (attribute.equals("POSITION"))
                vertexAttributes.add(VertexAttribute.Position());
            else if (attribute.equals("NORMAL"))
                vertexAttributes.add(VertexAttribute.Normal());
            else if (attribute.equals("COLOR"))
                vertexAttributes.add(VertexAttribute.ColorUnpacked());
            else if (attribute.equals("COLORPACKED"))
                vertexAttributes.add(VertexAttribute.ColorPacked());
            else if (attribute.equals("TANGENT"))
                vertexAttributes.add(VertexAttribute.Tangent());
            else if (attribute.equals("BINORMAL"))
                vertexAttributes.add(VertexAttribute.Binormal());
            else if (attribute.startsWith("TEXCOORD"))
                vertexAttributes.add(VertexAttribute.TexCoords(unit++));
            else if (attribute.startsWith("BLENDWEIGHT"))
                vertexAttributes.add(VertexAttribute.BoneWeight(blendWeightCount++));
            else
                throw new RuntimeException("Unknown vertex attribute '" + attribute
                        + "', should be one of position, normal, uv, tangent or binormal");
        }
        return vertexAttributes.toArray(new VertexAttribute[vertexAttributes.size()]);
    }
}
