import com.jogamp.opengl.GL2;
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

    OGLBuffers cube, grid;
    OGLTextRenderer textRenderer;

    //	uniform vec3 lightPos; //ve scene
//	uniform vec3 eyePos; //ve scene
    int cubeProgram, locCubeMat, locCubeLightPos, locCubeEyePos;
    int gridProgram, locGridMat, locGridLightPos, locGridEyePos;

    OGLTexture2D texture;

    Camera cam = new Camera();
    Mat4 proj;
    Vec3D lightPos = new Vec3D(3,2,5);


    OGLTexture2D.Viewer textureViewer;

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
        cubeProgram = ShaderUtils.loadProgram(gl, "/texture");
        gridProgram = ShaderUtils.loadProgram(gl, "/grid");

        createCube(gl);
        grid = GeometryGenerator.createGrid(gl, 10, 10, "inPosition");

        locCubeMat = gl.glGetUniformLocation(cubeProgram, "mat");
        locCubeLightPos = gl.glGetUniformLocation(cubeProgram, "lightPos");
        locCubeEyePos = gl.glGetUniformLocation(cubeProgram, "eyePos");

        locGridMat = gl.glGetUniformLocation(gridProgram, "mat");
        locGridLightPos = gl.glGetUniformLocation(gridProgram, "lightPos");
        locGridEyePos = gl.glGetUniformLocation(gridProgram, "eyePos");

        // load texture using JOGL objects
        // texture files are in /res/textures/

        texture = new OGLTexture2D(gl, "/textures/mosaic.jpg");

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125);

        gl.glEnable(GL2GL3.GL_DEPTH_TEST);
        textureViewer = new OGLTexture2D.Viewer(gl);
    }

    void createCube(GL2GL3 gl) {
        // vertices are not shared among triangles (and thus faces) so each face
        // can have a correct normal in all vertices
        // also because of this, the vertices can be directly drawn as GL_TRIANGLES
        // (three and three vertices form one face)
        // triangles defined in index buffer
        float[] cube = {
                // bottom (z-) face
                1, 0, 0,	0, 0, -1, 	1, 0,
                0, 0, 0,	0, 0, -1,	0, 0,
                1, 1, 0,	0, 0, -1,	1, 1,
                0, 1, 0,	0, 0, -1,	0, 1,
                // top (z+) face
                1, 0, 1,	0, 0, 1,	1, 0,
                0, 0, 1,	0, 0, 1,	0, 0,
                1, 1, 1,	0, 0, 1,	1, 1,
                0, 1, 1,	0, 0, 1,	0, 1,
                // x+ face
                1, 1, 0,	1, 0, 0,	1, 0,
                1, 0, 0,	1, 0, 0,	0, 0,
                1, 1, 1,	1, 0, 0,	1, 1,
                1, 0, 1,	1, 0, 0,	0, 1,
                // x- face
                0, 1, 0,	-1, 0, 0,	1, 0,
                0, 0, 0,	-1, 0, 0,	0, 0,
                0, 1, 1,	-1, 0, 0,	1, 1,
                0, 0, 1,	-1, 0, 0,	0, 1,
                // y+ face
                1, 1, 0,	0, 1, 0,	1, 0,
                0, 1, 0,	0, 1, 0,	0, 0,
                1, 1, 1,	0, 1, 0,	1, 1,
                0, 1, 1,	0, 1, 0,	0, 1,
                // y- face
                1, 0, 0,	0, -1, 0,	1, 0,
                0, 0, 0,	0, -1, 0,	0, 0,
                1, 0, 1,	0, -1, 0,	1, 1,
                0, 0, 1,	0, -1, 0,	0, 1
        };

        int[] indexBufferData = new int[36];
        for (int i = 0; i<6; i++){
            indexBufferData[i*6] = i*4;
            indexBufferData[i*6 + 1] = i*4 + 1;
            indexBufferData[i*6 + 2] = i*4 + 2;
            indexBufferData[i*6 + 3] = i*4 + 1;
            indexBufferData[i*6 + 4] = i*4 + 2;
            indexBufferData[i*6 + 5] = i*4 + 3;
        }


        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3),
                new OGLBuffers.Attrib("inTextureCoordinates", 2)
        };

        this.cube = new OGLBuffers(gl, cube, attributes, indexBufferData);
    }

    @Override
    public void display(GLAutoDrawable glDrawable) {
        GL2GL3 gl = glDrawable.getGL().getGL2GL3();

        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(cubeProgram);
        gl.glUniformMatrix4fv(locCubeMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);
        gl.glUniform3fv(locCubeLightPos, 1,ToFloatArray.convert(lightPos), 0);
        gl.glUniform3fv(locCubeEyePos, 1,ToFloatArray.convert(cam.getEye()), 0);


        texture.bind(cubeProgram, "textureID", 0);

        cube.draw(GL2GL3.GL_TRIANGLES, cubeProgram);

        gl.glUseProgram(gridProgram);
        gl.glUniformMatrix4fv(locGridMat, 1, false,
                ToFloatArray.convert(cam.getViewMatrix().mul(proj)), 0);
        gl.glUniform3fv(locGridLightPos, 1,ToFloatArray.convert(lightPos), 0);
        gl.glUniform3fv(locGridEyePos, 1,ToFloatArray.convert(cam.getEye()), 0);


        texture.bind(gridProgram, "textureID", 0);

        grid.draw(GL2GL3.GL_TRIANGLES, gridProgram);


        textureViewer.view(texture, -1, -1, 0.5);


        String text = new String(this.getClass().getName() + ": [LMB] camera, WSAD");

        textRenderer.drawStr2D(3, height-20, text);
        textRenderer.drawStr2D(width-150, 3, " (c) PGRF Jaroslav Langer");
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {
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
        glDrawable.getGL().getGL2GL3().glDeleteProgram(cubeProgram);
    }
}