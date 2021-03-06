package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.connection.ProviderType;
import com.jaimemartz.playerbalancer.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.settings.props.shared.SectionProps;
import com.jaimemartz.playerbalancer.utils.AlphanumComparator;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class ServerSection {
    private final String name;
    private final SectionProps props;

    private boolean inherited = false;
    private ServerSection parent;
    private int position;

    private transient ServerInfo server;
    private transient SectionCommand command;
    private transient AbstractProvider externalProvider;
    private Set<ServerInfo> servers;

    private boolean valid = false;

    public ServerSection(String name, SectionProps props) {
        this.name = name;
        this.props = props;

        this.servers = Collections.synchronizedNavigableSet(new TreeSet<>((lhs, rhs) ->
                AlphanumComparator.getInstance().compare(lhs.getName(), rhs.getName())
        ));
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SectionProps getProps() {
        return props;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public ServerSection getParent() {
        return parent;
    }

    public void setParent(ServerSection parent) {
        this.parent = parent;
    }

    public ProviderType getImplicitProvider() {
        if (inherited) {
            return parent.getImplicitProvider();
        } else {
            return props.getProvider();
        }

    }

    public ProviderType getExplicitProvider() {
        return props.getProvider();
    }

    public void setExplicitProvider(ProviderType provider) {
        props.setProvider(provider);
        inherited = false;
    }

    public void setExternalProvider(AbstractProvider externalProvider) {
        this.externalProvider = externalProvider;
    }

    public AbstractProvider getExternalProvider() {
        return externalProvider;
    }

    public ServerInfo getServer() {
        return server;
    }

    public void setServer(ServerInfo server) {
        this.server = server;
    }

    public SectionCommand getCommand() {
        return command;
    }

    public void setCommand(SectionCommand command) {
        this.command = command;
    }

    public Set<ServerInfo> getServers() {
        return servers;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "ServerSection{" +
                "name='" + name + '\'' +
                ", props=" + props +
                ", inherited=" + inherited +
                ", parent=" + parent +
                ", position=" + position +
                ", server=" + server +
                ", command=" + command +
                ", servers=" + servers +
                ", valid=" + valid +
                '}';
    }
}