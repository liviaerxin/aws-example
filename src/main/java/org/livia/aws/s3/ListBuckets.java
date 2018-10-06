package org.livia.aws.s3;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

public class ListBuckets {
	public static void main(String[] args)
    {
		String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
		String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
		if (accessKey == null) {
			System.out.println("accessKey can't be null");
			System.exit(1);
		}
		if (secretKey == null) {
			System.out.println("secretKey can't be null");
			System.exit(1);
		}
		System.out.println("accessKey is:" + accessKey);
		System.out.println("secretKey is:" + secretKey);
		AWSCredentials credentials = new BasicAWSCredentials(
				accessKey, 
				secretKey
				);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(credentials))
        		.withRegion(Regions.AP_NORTHEAST_2)
        		.build();
        
        List<Bucket> buckets = s3.listBuckets();
        System.out.println("Your Amazon S3 buckets are:");
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }
    }
}
