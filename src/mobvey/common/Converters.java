package mobvey.common;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author Shamo Humbatli
 */
public final class Converters {
    
    private static String defaultEncoding = "UTF-8";
    
    public static String XmlDocToString(Document doc, String encoding) throws TransformerConfigurationException, TransformerException, UnsupportedEncodingException {
        DOMSource domSource = new DOMSource(doc);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult sr = new StreamResult(new OutputStreamWriter(bos, encoding));

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        transformer.transform(domSource, sr);
        return new String(bos.toByteArray(), encoding);
    }
}
