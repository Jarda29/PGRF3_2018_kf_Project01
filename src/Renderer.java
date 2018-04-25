import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import ogltools.GeometryGenerator;
import oglutils.*;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.awt.event.*;

public class Renderer implements GLEventListener, MouseListener,
        MouseMotionListener, KeyListener {

    int width, height, ox, oy;

    OGLBuffers grid;
    OGLTextRenderer textRenderer;

    int gridProgram, locGridMat, locGridLightPos, locGridEyePos;

    int locSurfaceMode;

    OGLTexture2D texture;

    Camera cam = new Camera();
    Mat4 proj;
    Vec3D lightPos = new Vec3D(3,2,5);


    OGLTexture2D.Viewer textureViewer;

    private int surfaceModel = 0;
    private String[] surfaceModelText = {
            "Grid",
            "Koule",
            "Trubka",
            "Sloní hlava",
            "Sloní hlava",
            "Sombrero",
            "Sombrero"};

    private String[] textToBePrintedOnScreen = new String[2];

    @Override
    public void init(GLAutoDrawable glDrawable) {
        // check whether shaders are supported
        GL2GL3 gl = glDrawable.getGL().getGL2GL3();
        OGLUtils.shaderCheck(gl);

        // get and set debug version of GL class
        gl = OGLUtils.getDebugGL(gl);
        glDrawable.setGL(gl);

        OGLUtils.printOGLparameters(gl);

        textRenderer = new OGLTextRenderer(gl, glDrawable.getSurfaceWidth(), glDrawable.getSurfaceHeight());

        // shader files are in /shaders/ directory
        // shaders directory must be set as a source directory of the project
        // e.g. in Eclipse via main menu Project/Properties/Java Build Path/Source
        gridProgram = ShaderUtils.loadProgram(gl, "/grid");

        grid = GeometryGenerator.createGrid(gl, 20, 20, "inPosition");

        locGridMat = gl.glGetUniformLocation(gridProgram, "mat");
        locGridLightPos = gl.glGetUniformLocation(gridProgram, "lightPos");
        locGridEyePos = gl.glGetUniformLocation(gridProgram, "eyePos");
        locSurfaceMode = gl.glGetUniformLocation(gridProgram, "surfaceModel");

        // load texture using JOGL objects
        // texture files are in /res/textures/

        texture = new OGLTexture2D(gl, "/textures/mosaic.jpg");

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125);

        gl.glEnable(GL2GL3.GL_DEPTH_TEST);
        textureViewer = new OGLTexture2D.Viewer(gl);
    }


    @Override
    public void display(GLAutoDrawable glDrawable) {
        GL2GL3 gl = glDrawable.getGL().getGL2GL3();

        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);


        gl.glUseProgram(gridProgram);
        gl.glUniformMatrix4fv(locGridMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);
        gl.glUniform3fv(locGridLightPos, 1,ToFloatArray.convert(lightPos), 0);
        gl.glUniform3fv(locGridEyePos, 1,ToFloatArray.convert(cam.getEye()), 0);

        gl.glUniform1i(locSurfaceMode, surfaceModel);


        texture.bind(gridProgram, "textureID", 0);

        grid.draw(GL2GL3.GL_TRIANGLES, gridProgram);


        textureViewer.view(texture, -1, -1, 0.5);


        textToBePrintedOnScreen[0] = new String(this.getClass().getName() + ": [LMB] camera, WSAD");
        textToBePrintedOnScreen[1] = "Surface model [NUM 0-6]: "+surfaceModel + " - "+surfaceModelText[surfaceModel];
        displayText();
        textRenderer.drawStr2D(width-150, 3, " (c) PGRF Jaroslav Langer");
    }

    private void displayText(){
        for (int i=0;i<textToBePrintedOnScreen.length;i++) {
            textRenderer.drawStr2D(3, height-20-(i*20), textToBePrintedOnScreen[i]);
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
        textRenderer.updateSize(width, height);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ox = e.getX();
        oy = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        cam = cam.addAzimuth((double) Math.PI * (ox - e.getX()) / width)
                .addZenith((double) Math.PI * (e.getY() - oy) / width);
        ox = e.getX();
        oy = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                cam = cam.forward(1);
                break;
            case KeyEvent.VK_D:
                cam = cam.right(1);
                break;
            case KeyEvent.VK_S:
                cam = cam.backward(1);
                break;
            case KeyEvent.VK_A:
                cam = cam.left(1);
                break;
            case KeyEvent.VK_CONTROL:
                cam = cam.down(1);
                break;
            case KeyEvent.VK_SHIFT:
                cam = cam.up(1);
                break;
            case KeyEvent.VK_SPACE:
                cam = cam.withFirstPerson(!cam.getFirstPerson());
                break;
            case KeyEvent.VK_R:
                cam = cam.mulRadius(0.9f);
                break;
            case KeyEvent.VK_F:
                cam = cam.mulRadius(1.1f);
                break;

            case KeyEvent.VK_NUMPAD0:
                surfaceModel = 0;
                break;
            case KeyEvent.VK_NUMPAD1:
                surfaceModel = 1;
                break;
            case KeyEvent.VK_NUMPAD2:
                surfaceModel = 2;
                break;
            case KeyEvent.VK_NUMPAD3:
                surfaceModel = 3;
                break;
            case KeyEvent.VK_NUMPAD4:
                surfaceModel = 4;
                break;
            case KeyEvent.VK_NUMPAD5:
                surfaceModel = 5;
                break;
            case KeyEvent.VK_NUMPAD6:
                surfaceModel = 6;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void dispose(GLAutoDrawable glDrawable) {
        glDrawable.getGL().getGL2GL3().glDeleteProgram(gridProgram);
    }
}