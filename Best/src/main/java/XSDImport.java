import java.io.File;
import java.io.IOException;


import org.xml.sax.InputSource;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
/**
 * @author audunvennesland
 * 5. sep. 2017 
 */
public class XSDImport {

    public static void main(String[] args) throws IOException {

            String outputDirectory = "./files/java/";

            // Setup schema compiler
            SchemaCompiler sc = XJC.createSchemaCompiler();

            // Setup SAX InputSource
            File schemaFile = new File("./files/xsd/Item.xsd");
            InputSource is = new InputSource(schemaFile.toURI().toString());

            // Parse & build
            sc.parseSchema(is);
            S2JJAXBModel model = sc.bind();
            JCodeModel jCodeModel = model.generateCode(null, null);
            jCodeModel.build(new File(outputDirectory));

}

}
