package com.zdy.statistics.hadoop.register;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class ResgisterMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

	FileSplit fileSplit = null;
	private Map<String,String> reverseIndexMap;
	
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		String line = value.toString();
		String[] values = line.split(",");//register,user_id,device_id,register_time
		if(key.get() != 0){
			if("\"register\"".equals(values[0])){
				if(!reverseIndexMap.containsKey(values[2])){
					context.write(new Text("newDevice"), new IntWritable(1));
				}else{
					context.write(new Text("newDevice"), new IntWritable(0));
				}
			}
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
	}

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		reverseIndexMap = new HashMap<String, String>();
		
		URI[] paths = context.getCacheFiles();
		if(paths != null && paths.length > 0){
			FileSystem f= FileSystem.get(context.getConfiguration());
			if(f.exists(new Path(paths[0].toString()))){
				FileSystem fs = FileSystem.get(paths[0], context.getConfiguration());
				
				FSDataInputStream in = fs.open(new Path(paths[0]));
				
				String line;
				while((line = in.readLine()) != null){
					String[] values = line.split("\t");
					reverseIndexMap.put(values[0], values[1]);
				}
			}
			
		}else{
			System.out.println("file is not exists");
		}
		
	}
	
}