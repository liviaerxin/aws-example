package org.livia.aws.s3;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class RestGetObject {

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        try {
        	URL website = new URL("https://s3.ap-northeast-2.amazonaws.com/astri-kingmed/3dhistech/test.txt");
        	URLConnection con = website.openConnection();
        	con.setRequestProperty("x-amz-content-sha256", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        	con.setRequestProperty("x-amz-date", "20181006T161404Z");
        	con.setRequestProperty("Authorization", "AWS4-HMAC-SHA256 Credential=AKIAJP6YFKLWX2HYIV7Q/20181006/ap-northeast-2/s3/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=3f8cd3517f62925eb2a8c240248773cc7eded56cd8fa3bd941ec739973089bbd");
        	ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
        	FileOutputStream fos = new FileOutputStream("/Users/siyao/test.txt");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
    }
}
