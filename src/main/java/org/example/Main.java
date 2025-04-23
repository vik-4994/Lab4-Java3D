package org.example;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.vecmath.*;

import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {

    public Main() {
        setTitle("Solar System - Java 3D");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        getContentPane().add("Center", canvas3D);

        SimpleUniverse universe = new SimpleUniverse(canvas3D);
        ViewingPlatform vp = universe.getViewingPlatform();
        TransformGroup viewTransform = vp.getViewPlatformTransform();

        Transform3D view = new Transform3D();
        Vector3f eye = new Vector3f(1.8f, 1.2f, 2.8f);
        view.lookAt(
                new Point3d(eye),
                new Point3d(0, 0, 0),
                new Vector3d(0, 1, 0)
        );
        view.invert();
        viewTransform.setTransform(view);


        OrbitBehavior orbit = new OrbitBehavior(canvas3D);
        orbit.setSchedulingBounds(new BoundingSphere());
        universe.getViewingPlatform().setViewPlatformBehavior(orbit);

        BranchGroup scene = createSceneGraph();
        scene.compile();
        universe.addBranchGraph(scene);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);

        AmbientLight ambientLight = new AmbientLight(lightColor);
        ambientLight.setInfluencingBounds(bounds);
        root.addChild(ambientLight);

        PointLight pointLight = new PointLight(lightColor, new Point3f(0f, 0f, 0f), new Point3f(1f, 0f, 0f));
        pointLight.setInfluencingBounds(bounds);
        root.addChild(pointLight);

        Appearance sunApp = new Appearance();
        ColoringAttributes sunColor = new ColoringAttributes(new Color3f(Color.YELLOW), ColoringAttributes.SHADE_FLAT);
        sunApp.setColoringAttributes(sunColor);
        Sphere sun = createTexturedPlanet("textures/sun.jpeg", 0.3f);
        root.addChild(sun);

        Appearance earthApp = new Appearance();
        earthApp.setColoringAttributes(new ColoringAttributes(new Color3f(Color.BLUE), ColoringAttributes.SHADE_FLAT));
        Sphere earth = createTexturedPlanet("textures/earth.jpg", 0.1f);


        TransformGroup orbitEarth = new TransformGroup();
        orbitEarth.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D earthPos = new Transform3D();
        earthPos.setTranslation(new Vector3f(0.8f, 0f, 0f));
        TransformGroup earthMove = new TransformGroup(earthPos);
        earthMove.addChild(earth);

        orbitEarth.addChild(earthMove);

        Appearance moonApp = new Appearance();
        Material moonMat = new Material(
                new Color3f(Color.LIGHT_GRAY),
                new Color3f(Color.BLACK),
                new Color3f(Color.GRAY),
                new Color3f(Color.WHITE),
                32.0f
        );
        moonApp.setMaterial(moonMat);

        Sphere moon = createTexturedPlanet("textures/moon.jpg", 0.03f);


        TransformGroup moonOrbit = new TransformGroup();
        moonOrbit.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D moonPos = new Transform3D();
        moonPos.setTranslation(new Vector3f(0.2f, 0f, 0f));
        TransformGroup moonMove = new TransformGroup(moonPos);
        moonMove.addChild(moon);
        moonOrbit.addChild(moonMove);

        Alpha moonAlpha = new Alpha(-1, 2000);
        RotationInterpolator moonRot = new RotationInterpolator(moonAlpha, moonOrbit);
        moonRot.setSchedulingBounds(new BoundingSphere());

        earthMove.addChild(moonOrbit);
        earthMove.addChild(moonRot);

        Sphere venus = createTexturedPlanet("textures/venus.jpg", 0.09f);
        TransformGroup orbitVenus = new TransformGroup();
        orbitVenus.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D venusPos = new Transform3D();
        venusPos.setTranslation(new Vector3f(0.5f, 0f, 0f));
        TransformGroup venusMove = new TransformGroup(venusPos);
        venusMove.addChild(venus);
        orbitVenus.addChild(venusMove);

        Alpha venusAlpha = new Alpha(-1, 7000);
        RotationInterpolator rotVenus = new RotationInterpolator(venusAlpha, orbitVenus);
        rotVenus.setSchedulingBounds(new BoundingSphere());

        root.addChild(orbitVenus);
        root.addChild(rotVenus);

        Sphere saturn = createTexturedPlanet("textures/saturn.jpeg", 0.15f);

        TransformGroup orbitSaturn = new TransformGroup();
        orbitSaturn.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D saturnPos = new Transform3D();
        saturnPos.setTranslation(new Vector3f(1.5f, 0f, 0f));
        TransformGroup saturnMove = new TransformGroup(saturnPos);
        saturnMove.addChild(saturn);
        orbitSaturn.addChild(saturnMove);

        Alpha saturnAlpha = new Alpha(-1, 12000);
        RotationInterpolator rotSaturn = new RotationInterpolator(saturnAlpha, orbitSaturn);
        rotSaturn.setSchedulingBounds(new BoundingSphere());

        root.addChild(orbitSaturn);
        root.addChild(rotSaturn);


        Alpha earthAlpha = new Alpha(-1, 5000);
        RotationInterpolator rotEarth = new RotationInterpolator(earthAlpha, orbitEarth);
        rotEarth.setSchedulingBounds(new BoundingSphere());
        root.addChild(rotEarth);

        root.addChild(orbitEarth);

        return root;
    }

    private Sphere createTexturedPlanet(String texturePath, float radius) {
        Appearance appearance = new Appearance();
        TextureLoader loader = new TextureLoader(
                getClass().getClassLoader().getResource(texturePath),
                "RGB", new Container()
        );

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