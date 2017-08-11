package de.dbtest.persistence;

import de.dbtest.common.UniversalDatabaseConnector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Lennard Scheffler
 */
public class Configuration {

    private Set<UniversalDatabaseConnector> targets;

    public Configuration() {
        this.targets = new HashSet<UniversalDatabaseConnector>();
    }

    public Set<UniversalDatabaseConnector> getTargets() {
        return targets;
    }

    public void setTargets(Set<UniversalDatabaseConnector> targets) {
        this.targets = targets;
    }

    public boolean addTarget(UniversalDatabaseConnector target) {
        return targets.add(target);
    }

    public UniversalDatabaseConnector getTarget(String identifier) {
        List<UniversalDatabaseConnector> l = targets.stream().filter(t -> t.getIdentifier().equals(identifier)).collect(Collectors.toList());

        if (l.size() == 0) return null;

        return l.get(0);
    }

    public boolean removeTarget(String identifier) {
        return targets.remove(getTarget(identifier));
    }
}
