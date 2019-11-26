package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.views.Request;

/**
 * Created by itrs on 10/21/2019.
 */
public class AnprRequest implements Request {

    public Long id;

    public AnprRequest(Long id){
        this.id=id;
    }

    public AnprRequest(){

    }

    public Long getId() {
        return unmask(id);
    }
}
