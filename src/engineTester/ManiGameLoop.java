package engineTester;

import Guis.GuiRenderer;
import Guis.GuiShader;
import Guis.GuiTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import models.RawModel;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;

import java.util.ArrayList;
import java.util.List;

public class ManiGameLoop {
    public static void main(String[] args) {

        DisplayManager.createDisplay();

        Loader loader = new Loader();

        ModelData data = OBJFileLoader.loadOBJ("stall");
        RawModel rawModel = loader.loadToVao(data.getVertices(), data.getTextureCoords(), data.getIndices(), data.getNormals());
        ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
        TexturedModel texturedModel = new TexturedModel(rawModel, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        ModelData data2 = OBJFileLoader.loadOBJ("lamp");
        RawModel rawModel2 = loader.loadToVao(data2.getVertices(), data2.getTextureCoords(), data2.getIndices(), data2.getNormals());
        ModelTexture texture2 = new ModelTexture(loader.loadTexture("lamp"));
        TexturedModel lamp = new TexturedModel(rawModel2, texture2);
        texture2.setShineDamper(10);
        texture2.setReflectivity(1);
        Entity lampE =  new Entity(lamp, new Vector3f(-20,0,-20),0,0,0,1);

        Light light = new Light( new Vector3f(0,30, -200), new Vector3f(0.4f,0.4f,0.4f));
        List<Light> lights = new ArrayList<>();
        lights.add(light);
        lights.add( new Light(new Vector3f(-20,13,-20), new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));
//        lights.add( new Light(new Vector3f(200,10,200), new Vector3f(0,0,10)));


        //terrain textures
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrains/grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrains/dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrains/grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrains/path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrains/blendMap"));

        Terrain terrain = new Terrain (0, -1, loader,texturePack, blendMap, "heightMap");
      Terrain terrain2 = new Terrain (-1, -1, loader,texturePack, blendMap, "heightMap");

        //collision detection vid; https://www.youtube.com/watch?v=6Nkn1x9DSSE

        MasterRenderer renderer = new MasterRenderer();


        ModelData data1 = OBJFileLoader.loadOBJ("test2");
        RawModel rawModel1 = loader.loadToVao(data1.getVertices(), data1.getTextureCoords(), data1.getIndices(), data1.getNormals());
        ModelTexture texture1 = new ModelTexture(loader.loadTexture("test2"));
        TexturedModel texturedModel1 = new TexturedModel(rawModel1, texture1);

        Player player = new Player(texturedModel1, new Vector3f(0,0,20),0,180,0,1);
        Entity stall = new Entity(texturedModel, new Vector3f(-10,1,-100),0,-90,0,1);
        Camera camera = new Camera(player);

        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(0.5f,0.5f), new Vector2f(0.25f,0.25f));
        guis.add(gui);

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        MousePicker picker = new MousePicker(renderer.getProjectionMatrix(), camera);

        while(!Display.isCloseRequested()) {
//            player.increaseRotation(0,1,0);
            player.move(terrain);
            camera.move();
            picker.update();
            System.out.println(picker.getCurrentRay());
            renderer.processEntity(player);
            renderer.processTerrain(terrain);
           renderer.processTerrain(terrain2);

            renderer.processEntity(stall);
            renderer.processEntity(lampE);

            renderer.render(lights, camera);

            //render gui
            guiRenderer.render(guis);
            DisplayManager.updateDisplay();

        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
