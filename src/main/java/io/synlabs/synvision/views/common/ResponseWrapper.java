package io.synlabs.synvision.views.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseWrapper<T> {

    List<T> data = new ArrayList<T>();

    public long totalElements;
    public int currPage;

}
