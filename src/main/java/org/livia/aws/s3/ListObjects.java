package org.livia.aws.s3;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class ListObjects {
	public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "To run this example, supply the name of a bucket to list!\n" +
            "\n" +
            "Ex: ListObjects <bucket-name>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucket_name = args[0];

        System.out.format("Objects in S3 bucket %s:\n", bucket_name);

		String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
		String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
		AWSCredentials credentials = new BasicAWSCredentials(
				accessKey, 
				secretKey
				);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
      		.withCredentials(new AWSStaticCredentialsProvider(credentials))
      		.withRegion(Regions.AP_NORTHEAST_2)
      		.build();
      
        ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os: objects) {
            System.out.println("* " + os.getKey());
        }
    }

}
