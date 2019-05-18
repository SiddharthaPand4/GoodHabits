package io.synlabs.atcc.views;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UploadFileResponse {

    private String fileName;
    private String fileType;
    private long size;
    private String tag;

    public UploadFileResponse(String fileName, String fileType, long size, String tag) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
        this.tag = tag;
    }
}
