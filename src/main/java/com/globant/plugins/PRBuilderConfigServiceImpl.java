package com.globant.plugins;

import java.util.List;
import com.atlassian.activeobjects.external.ActiveObjects;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;


public class PRBuilderConfigServiceImpl implements PRBuilderConfigService
{
    private final ActiveObjects ao;

    public PRBuilderConfigServiceImpl(ActiveObjects ao)
    {
        this.ao = checkNotNull(ao);
    }

    @Override
    public PRBuilderConfig add(int planId, String branch)
    {
        final PRBuilderConfig prbc = ao.create(PRBuilderConfig.class);
        prbc.setPlanId(planId);
        prbc.setBranch(branch);
        prbc.save();
        return prbc;
    }

    @Override
    public PRBuilderConfig delete(int prbcId)
    {
        final PRBuilderConfig prbc = ao.get(PRBuilderConfig.class, prbcId);
        ao.delete(prbc);
        return prbc;
    }

    @Override
    public List<PRBuilderConfig> all()
    {
        return newArrayList(ao.find(PRBuilderConfig.class));
    }
}
