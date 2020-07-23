package io.synlabs.synvision.views.core;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Menu {

    private Set<MenuItem> items = new LinkedHashSet<>();
          public void merge(MenuItem menu,MenuItem child) {

             if (items.contains(menu)){
                     for (MenuItem parent : items) {
                        if (parent.equals(menu)) {
                         parent.getSubmenu().add(child);
                     }
                 }
             }
             else {
                MenuItem parent=new MenuItem(menu);
                parent.getSubmenu().add(child);
                items.add(parent);
             }
          }

    public void add(MenuItem parent) {
        if (items.contains(parent)) {
        }
        else {
            //add the whole tree once
            items.add(parent);
        }
    }
    // public void merge(MenuItem parent,MenuItem submenu) {

  //     if (items.contains(parent)) {

  //         //toplevel is already there, find the right top level and merge children
  //         for (MenuItem toplevel : items.value) {

  //             if (toplevel.equals(parent)) {
  //                 toplevel.merge(submenu);
  //             }
  //         }
  //     }
  //     else {
  //         //add the whole tree once
  //         items.add(parent,submenu);
  //     }
  // }
}
