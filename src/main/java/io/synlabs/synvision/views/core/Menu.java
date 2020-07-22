package io.synlabs.synvision.views.core;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Menu {

    private Set<MenuItem> items = new LinkedHashSet<>();

    public void add(MenuItem parent,MenuItem menuItem) {
        MenuItem addedParent = null;
        if (!items.contains(parent)) {
            addedParent = new MenuItem(parent);
            items.add(addedParent);
        } else {
            for (MenuItem mainItem : items) {
                if (parent.equals(mainItem)) {
                    addedParent = mainItem;
                }
            }
        }
        if (addedParent != null)
            addedParent.getSubmenu().add(menuItem);

    }


//  public void merge(MenuItem item) {

//      if (items.contains(item)) {

//          //toplevel is already there, find the right top level and merge children
//          for (MenuItem toplevel : items) {

//              if (toplevel.equals(item)) {
//                  toplevel.merge(item.getSubmenu());
//              }
//          }
//      }
//      else {
//          //add the whole tree once
//          items.add(item);
//      }
//  }
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
