package io.synlabs.synvision.views.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MenuItem {

    private String key;
    private String title;
    private String icon;
    private String link;
    private String seq;
    private String privilege;
    private Set<MenuItem> submenu = new LinkedHashSet<>();
    private String parent;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return key.equals(menuItem.key);
    }
    public void setParent(String parent)
    {
        this.parent = parent;
    }
    public MenuItem(MenuItem copy)
    {
        this.key = copy.getKey();
        this.link = copy.getLink();
        this.icon = copy.getIcon();
        this.title = copy.getTitle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public void merge(Set<MenuItem> children) {
        submenu.addAll(children);
    }
}
