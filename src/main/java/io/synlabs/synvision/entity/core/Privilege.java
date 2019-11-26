package io.synlabs.synvision.entity.core;

import io.synlabs.synvision.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Privilege extends BaseEntity
{

  @Column(nullable = false, length = 50)
  private String name;

  public Privilege()
  {
  }

  public Privilege(Privilege p)
  {
    this.name = p.name;
  }

}
