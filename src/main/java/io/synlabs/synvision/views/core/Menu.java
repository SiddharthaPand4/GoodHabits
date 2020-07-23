package io.synlabs.synvision.views.core;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Menu {

    private Set<MenuItem> items = new LinkedHashSet<>();

    public void merge(MenuItem menu, MenuItem child) {
        if (items.contains(menu)) {
            for (MenuItem parent : items) {
                if (parent.equals(menu)) {
                    parent.getSubmenu().add(child);
                }
            }
        } else {
            MenuItem parent = new MenuItem(menu);
            parent.getSubmenu().add(child);
            items.add(parent);
        }
    }

    public void add(MenuItem parent) {
        if (items.contains(parent)) {
        } else {
            //add the whole tree once
            items.add(parent);
        }
    }
}
