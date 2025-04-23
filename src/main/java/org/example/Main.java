package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Primitive;

import javax.media.j3d.*;
import javax.vecmath.*;

import java.awt.*;
import javax.swing.*;
import java.util.Enumeration;

public class Main extends JFrame {

    public Main() {
        // Налаштування вікна
        setTitle("Solar System - Java 3D");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Canvas 3D
        Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        getContentPane().add("Center", canvas3D);

        // Простір (універсум)
        SimpleUniverse universe = new SimpleUniverse(canvas3D);
        universe.getViewingPlatform().setNominalViewingTransform();

        // Орбітальна поведінка (управління камерою мишкою)
        OrbitBehavior orbit = new OrbitBehavior(canvas3D);
        orbit.setSchedulingBounds(new BoundingSphere());
        universe.getViewingPlatform().setViewPlatformBehavior(orbit);

        // Головна сцена
        BranchGroup scene = createSceneGraph();
        scene.compile();
        universe.addBranchGraph(scene);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        // Світло
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);

        AmbientLight ambientLight = new AmbientLight(lightColor);
        ambientLight.setInfluencingBounds(bounds);
        root.addChild(ambientLight);

        PointLight pointLight = new PointLight(lightColor, new Point3f(0f, 0f, 0f), new Point3f(1f, 0f, 0f));
        pointLight.setInfluencingBounds(bounds);
        root.addChild(pointLight);

        // Сонце (жовта сфера)
        Appearance sunApp = new Appearance();
        ColoringAttributes sunColor = new ColoringAttributes(new Color3f(Color.YELLOW), ColoringAttributes.SHADE_FLAT);
        sunApp.setColoringAttributes(sunColor);
        Sphere sun = new Sphere(0.3f, Primitive.GENERATE_NORMALS, 50, sunApp);
        root.addChild(sun);

        // Планета (наприклад, Земля)
        // Планета (наприклад, Земля)
        Appearance earthApp = new Appearance();
        earthApp.setColoringAttributes(new ColoringAttributes(new Color3f(Color.BLUE), ColoringAttributes.SHADE_FLAT));
        Sphere earth = new Sphere(0.1f, Primitive.GENERATE_NORMALS, 50, earthApp);

// Орбіта Землі
        TransformGroup orbitEarth = new TransformGroup();
        orbitEarth.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D earthPos = new Transform3D();
        earthPos.setTranslation(new Vector3f(0.8f, 0f, 0f));
        TransformGroup earthMove = new TransformGroup(earthPos);
        earthMove.addChild(earth);

        orbitEarth.addChild(earthMove);

        // Місяць
        Appearance moonApp = new Appearance();
        Material moonMat = new Material(
                new Color3f(Color.LIGHT_GRAY),
                new Color3f(Color.BLACK),
                new Color3f(Color.GRAY),
                new Color3f(Color.WHITE),
                32.0f
        );
        moonApp.setMaterial(moonMat);

        Sphere moon = new Sphere(0.03f, Primitive.GENERATE_NORMALS, 50, moonApp);

// Орбіта Місяця навколо Землі
        TransformGroup moonOrbit = new TransformGroup();
        moonOrbit.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D moonPos = new Transform3D();
        moonPos.setTranslation(new Vector3f(0.2f, 0f, 0f));
        TransformGroup moonMove = new TransformGroup(moonPos);
        moonMove.addChild(moon);
        moonOrbit.addChild(moonMove);

// Додати анімацію обертання Місяця
        Alpha moonAlpha = new Alpha(-1, 2000);
        RotationInterpolator moonRot = new RotationInterpolator(moonAlpha, moonOrbit);
        moonRot.setSchedulingBounds(new BoundingSphere());

// Додати місячну орбіту до Землі
        earthMove.addChild(moonOrbit);
        earthMove.addChild(moonRot);



        // Анімація обертання Землі навколо Сонця
        Alpha earthAlpha = new Alpha(-1, 5000);
        RotationInterpolator rotEarth = new RotationInterpolator(earthAlpha, orbitEarth);
        rotEarth.setSchedulingBounds(new BoundingSphere());
        root.addChild(rotEarth);

        root.addChild(orbitEarth);

        return root;
    }

    private Sphere createTexturedPlanet(String texturePath, float radius) {
        Appearance appearance = new Appearance();
        TextureLoader loader = new TextureLoader(texturePath, "RGB", new Container());
        Texture texture = loader.getTexture();
        appearance.setTexture(texture);

        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTextureAttributes(texAttr);

        return new Sphere(radius, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS, 50, appearance);
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Main window = new Main();
            window.setVisible(true);
        });
    }
}