/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package mazegen.ui;

public class MoleculeSampleWindow extends Window {

//    //// Why. Why do we have so many instance variables. What is this.
//    final Group root = new Group();
//    final Xform axisGroup = new Xform();
//    final Xform moleculeGroup = new Xform();
//    final Xform world = new Xform();
//    final PerspectiveCamera camera = new PerspectiveCamera(true);
//    final Xform cameraXform = new Xform();
//    final Xform cameraXform2 = new Xform();
//    final Xform cameraXform3 = new Xform();
//    private static final double CAMERA_INITIAL_DISTANCE = -450;
//    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
//    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
//    private static final double CAMERA_NEAR_CLIP = 0.1;
//    private static final double CAMERA_FAR_CLIP = 10000.0;
//    private static final double AXIS_LENGTH = 250.0;
//    private static final double HYDROGEN_ANGLE = 104.5;
//    private static final double CONTROL_MULTIPLIER = 0.1;
//    private static final double SHIFT_MULTIPLIER = 10.0;
//    private static final double MOUSE_SPEED = 0.1;
//    private static final double ROTATION_SPEED = 2.0;
//    private static final double TRACK_SPEED = 0.3;
//
//    double mousePosX;
//    double mousePosY;
//    double mouseOldX;
//    double mouseOldY;
//    double mouseDeltaX;
//    double mouseDeltaY;
//
//    public MoleculeSampleWindow() {
//        //// Dunno what this is
//        root.getChildren().add(world);
//        root.setDepthTest(DepthTest.ENABLE);
//
//
//        //// Set up camera
//        root.getChildren().add(cameraXform);
//        cameraXform.getChildren().add(cameraXform2);
//        cameraXform2.getChildren().add(cameraXform3);
//        cameraXform3.getChildren().add(camera);
//        cameraXform3.setRotateZ(180.0);
//
//        camera.setNearClip(CAMERA_NEAR_CLIP);
//        camera.setFarClip(CAMERA_FAR_CLIP);
//        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
//        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
//        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
//
//        //// Colors
//        final PhongMaterial redMaterial = new PhongMaterial();
//        redMaterial.setDiffuseColor(Color.DARKRED);
//        redMaterial.setSpecularColor(Color.RED);
//
//        final PhongMaterial greenMaterial = new PhongMaterial();
//        greenMaterial.setDiffuseColor(Color.DARKGREEN);
//        greenMaterial.setSpecularColor(Color.GREEN);
//
//        final PhongMaterial blueMaterial = new PhongMaterial();
//        blueMaterial.setDiffuseColor(Color.DARKBLUE);
//        blueMaterial.setSpecularColor(Color.BLUE);
//
//        final PhongMaterial whiteMaterial = new PhongMaterial();
//        whiteMaterial.setDiffuseColor(Color.WHITE);
//        whiteMaterial.setSpecularColor(Color.LIGHTBLUE);
//
//        final PhongMaterial greyMaterial = new PhongMaterial();
//        greyMaterial.setDiffuseColor(Color.DARKGREY);
//        greyMaterial.setSpecularColor(Color.GREY);
//
//        //// Set up axes
//        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
//        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
//        final Box zAxis = new Box(1, 1, AXIS_LENGTH);
//
//        xAxis.setMaterial(redMaterial);
//        yAxis.setMaterial(greenMaterial);
//        zAxis.setMaterial(blueMaterial);
//
//        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
//        axisGroup.setVisible(false); // I guess we don't show the axes?
//        world.getChildren().addAll(axisGroup);
//
//        //// Set up the molecule model
//        // Molecule Hierarchy
//        // [*] moleculeXform
//        //     [*] oxygenXform
//        //         [*] oxygenSphere
//        //     [*] hydrogen1SideXform
//        //         [*] hydrogen1Xform
//        //             [*] hydrogen1Sphere
//        //         [*] bond1Cylinder
//        //     [*] hydrogen2SideXform
//        //         [*] hydrogen2Xform
//        //             [*] hydrogen2Sphere
//        //         [*] bond2Cylinder
//        Xform moleculeXform = new Xform();
//        Xform oxygenXform = new Xform();
//        Xform hydrogen1SideXform = new Xform();
//        Xform hydrogen1Xform = new Xform();
//        Xform hydrogen2SideXform = new Xform();
//        Xform hydrogen2Xform = new Xform();
//
//        Sphere oxygenSphere = new Sphere(40.0);
//        oxygenSphere.setMaterial(redMaterial);
//
//        Sphere hydrogen1Sphere = new Sphere(30.0);
//        hydrogen1Sphere.setMaterial(whiteMaterial);
//        hydrogen1Sphere.setTranslateX(0.0);
//
//        Sphere hydrogen2Sphere = new Sphere(30.0);
//        hydrogen2Sphere.setMaterial(whiteMaterial);
//        hydrogen2Sphere.setTranslateZ(0.0);
//
//        Cylinder bond1Cylinder = new Cylinder(5, 100);
//        bond1Cylinder.setMaterial(greyMaterial);
//        bond1Cylinder.setTranslateX(50.0);
//        bond1Cylinder.setRotationAxis(Rotate.Z_AXIS);
//        bond1Cylinder.setRotate(90.0);
//
//        Cylinder bond2Cylinder = new Cylinder(5, 100);
//        bond2Cylinder.setMaterial(greyMaterial);
//        bond2Cylinder.setTranslateX(50.0);
//        bond2Cylinder.setRotationAxis(Rotate.Z_AXIS);
//        bond2Cylinder.setRotate(90.0);
//
//        moleculeXform.getChildren().add(oxygenXform);
//        moleculeXform.getChildren().add(hydrogen1SideXform);
//        moleculeXform.getChildren().add(hydrogen2SideXform);
//        oxygenXform.getChildren().add(oxygenSphere);
//        hydrogen1SideXform.getChildren().add(hydrogen1Xform);
//        hydrogen2SideXform.getChildren().add(hydrogen2Xform);
//        hydrogen1Xform.getChildren().add(hydrogen1Sphere);
//        hydrogen2Xform.getChildren().add(hydrogen2Sphere);
//        hydrogen1SideXform.getChildren().add(bond1Cylinder);
//        hydrogen2SideXform.getChildren().add(bond2Cylinder);
//
//        hydrogen1Xform.setTx(100.0);
//        hydrogen2Xform.setTx(100.0);
//        hydrogen2SideXform.setRotateY(HYDROGEN_ANGLE);
//
//        moleculeGroup.getChildren().add(moleculeXform);
//
//        world.getChildren().addAll(moleculeGroup);
//    }
//
//    @Override
//    public String getTitle() {
//        return "Molecule Sample Application";
//    }
//
//    @Override
//    protected int getDefaultWidth() {
//        return 1024;
//    }
//
//    @Override
//    protected int getDefaultHeight() {
//        return 768;
//    }
//
//    @Override
//    public Parent getUI() {
//        return root;
//    }
//
//    @Override
//    protected void handleMousePressed(MouseEvent me) {
//        mousePosX = me.getSceneX();
//        mousePosY = me.getSceneY();
//        mouseOldX = me.getSceneX();
//        mouseOldY = me.getSceneY();
//    }
//
//    @Override
//    protected void handleMouseDragged(MouseEvent me) {
//        mouseOldX = mousePosX;
//        mouseOldY = mousePosY;
//        mousePosX = me.getSceneX();
//        mousePosY = me.getSceneY();
//        mouseDeltaX = (mousePosX - mouseOldX);
//        mouseDeltaY = (mousePosY - mouseOldY);
//
//        double modifier = 1.0;
//
//        if (me.isControlDown()) {
//            modifier = CONTROL_MULTIPLIER;
//        }
//        if (me.isShiftDown()) {
//            modifier = SHIFT_MULTIPLIER;
//        }
//        if (me.isPrimaryButtonDown()) {
//            cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);
//            cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);
//        }
//        else if (me.isSecondaryButtonDown()) {
//            double z = camera.getTranslateZ();
//            double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
//            camera.setTranslateZ(newZ);
//        }
//        else if (me.isMiddleButtonDown()) {
//            cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);
//            cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);
//        }
//    }
//
//    @Override
//    protected void handleKeyPressed(KeyEvent event) {
//        switch (event.getCode()) {
//            case Z:
//                cameraXform2.t.setX(0.0);
//                cameraXform2.t.setY(0.0);
//                camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
//                cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
//                cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
//                break;
//            case X:
//                axisGroup.setVisible(!axisGroup.isVisible());
//                break;
//            case V:
//                moleculeGroup.setVisible(!moleculeGroup.isVisible());
//                break;
//        }
//    }
//
//    @Override
//    public Color getFill() {
//        return Color.GREY;
//    }
//
//    @Override
//    protected Camera getCamera() {
//        return camera;
//    }

}
