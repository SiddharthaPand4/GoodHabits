package io.synlabs.synvision.views.core;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class MenuItem {

    private String key;
    private String title;
    private String icon;
    private String link;
    private String seq;

    private Set<MenuItem> submenu = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return key.equals(menuItem.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public void merge(Set<MenuItem> children) {
        submenu.addAll(children);
    }
}
