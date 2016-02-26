package com.globant.plugins;

import java.util.List;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.bamboo.plan.Plan;


/**
 * Created by claudio.melendrez on 22/02/16.
 *
 * AO Service for PRBuilderConfig objects.
 */
@Transactional
public interface PRBuilderConfigService
{

    PRBuilderConfig add(int planId, String userName);
    PRBuilderConfig delete(int prbcId);

    List<PRBuilderConfig> all();
}

