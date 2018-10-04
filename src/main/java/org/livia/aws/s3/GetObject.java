package org.livia.aws.s3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class GetObject {
	
	public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "To run this example, supply the name of an S3 bucket and object to\n" +
            "download from it.\n" +
            "\n" +
            "Ex: GetObject <bucketname> <filename>\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucket_name = args[0];
        String key_name = args[1];

        System.out.format("Downloading %s from S3 bucket %s...\n", key_name, bucket_name);
        
        AWSCredentials credentials = new BasicAWSCredentials(
				  "AKIAJP4O7IMPNHRWNUWQ", 
				  "PsWiedH71JW/BYXUUYBxPemUY4N6QFtcNOv7ITfj"
				);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
      		.withCredentials(new AWSStaticCredentialsProvider(credentials))
      		.withRegion(Regions.AP_NORTHEAST_2)
      		.build();
      
        try {
            S3Object o = s3.getObject(bucket_name, key_name);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File("/tmp", key_name));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
