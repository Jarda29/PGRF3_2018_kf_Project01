package ogltools;

import com.jogamp.opengl.GL2GL3;
import oglutils.OGLBuffers;
import oglutils.ToFloatArray;
import oglutils.ToIntArray;
import transforms.Vec2D;

import java.util.ArrayList;
import java.util.List;

public class GeometryGenerator {
    public static OGLBuffers createGrid(
            final GL2GL3 gl,
            final int cols,
            final int rows,
            final String variable) {
        final List<Vec2D> vertices = new ArrayList<>();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                vertices.add(
                        new Vec2D((double) c / cols, (double) r / rows));
        final List<Integer> indices = new ArrayList<>();
        for (int r = 0; r < rows - 1; r++)
            for (int c = 0; c < cols - 1; c++) {
                indices.add(r * cols + c);
                indices.add((r + 1) * cols + c);
                indices.add(r * cols + c + 1);
                indices.add((r + 1) * cols + c);
                indices.add(r * cols + c + 1);
                indices.add((r + 1) * cols + c + 1);
            }
        final OGLBuffers.Attrib[] attribs = {
                new OGLBuffers.Attrib(variable, 2)
        };
        return new OGLBuffers(gl,
                ToFloatArray.convert(vertices),
                attribs,
                ToIntArray.convert(indices));
    }
}
