package io.synlabs.synvision.views.core;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class Menu {

    private boolean admin;
    private Set<MenuItem> items = new LinkedHashSet<>();

    public void merge(MenuItem item) {

        if (items.contains(item)) {

            //toplevel is already there, find the right top level and merge children
            for (MenuItem toplevel : items) {

                if (toplevel.equals(item)) {
                    toplevel.merge(item.getSubmenu());
                }
            }
        }
        else {
            //add the whole tree once
            items.add(item);
        }
    }
}
