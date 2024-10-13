package render;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private Boolean beenUsed = false;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public  Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            //find the first pattern after #type 'pattern'
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\n", index);
            String firstPattern = source.substring(index, eol).trim();

            //find the second pattern after #type 'pattern'
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource= splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource= splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern+ "'");
            }

        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'";
        }
        System.out.println(vertexSource);
        System.out.println(fragmentSource);
    }

    public void compile() {

        int vertexID, fragmentID;
        //compile and link shaders
        // first load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //pass the shader source code
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        //check for errors in compilation process
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR:'" + filepath + "'\n\tVertexShader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : " ";
        }

        //compile and link shaders
        // first load and compile the fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //pass the shader source code
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        //check for errors in compilation process
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragmentShader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : " ";
        }

        //link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath +"'\n\tLinking shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : " ";
        }

    }

    public void use() {
        if (!beenUsed) {
            //bind shader program
            glUseProgram(shaderProgramID);
            beenUsed = true;
        }
    }

    public void detach() {
    glUseProgram(0);
    beenUsed = false;
    }

    public  void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);//[1,1,1,...,1] buffer. 16bytes
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public  void  uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);

    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);

    }

}






















