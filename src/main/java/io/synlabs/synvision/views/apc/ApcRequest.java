package io.synlabs.synvision.views.apc;

import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApcRequest implements Request{
  public  Long id;
  public ApcRequest(Long id){
      this.id=id;
  }
    public Long getId() {

        return unmask(id);
    }
}
