package com.function;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RequestProperties {
	@JsonProperty("resourceGroup")
	String resourceGroup;
	@JsonProperty("storageAccount")
    String storageAccount;
	@JsonProperty("fileShare")
	String fileShare;
	@JsonProperty("readers")
	List<String> readers = new ArrayList<>();

	@JsonProperty("authors")
	List<String> authors = new ArrayList<>();

	public String getResourceGroup(){return  this.resourceGroup;}

	public String getStorageAccount() {
		return this.storageAccount;
	}
	public String getFileShare() {
		return this.fileShare;
	}
	public List<String> getReaders() {
		return this.readers;
	}
	public List<String> getAuthors() {
		return this.authors;
	}

	public void setResourceGroup(String resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	public void setStorageAccount(String storageAccount) {
		this.storageAccount = storageAccount;
	}
	public void setFileShare(String fileShare) {
		this.fileShare = fileShare;
	}
	public void setReaders(List<String> readers) {
		this.readers = readers;
	}
	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}
}
