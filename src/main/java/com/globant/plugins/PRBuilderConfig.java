package com.globant.plugins;

import net.java.ao.Entity;
import net.java.ao.Preload;

import com.atlassian.bamboo.plan.Plan;

import java.util.List;


/**
 * Created by claudio.melendrez on 22/02/16.
 *
 * Stores a pull request builder configuration key data.
 */
@Preload
public interface PRBuilderConfig extends Entity
{

    int getPlanId();
    void setPlanId(int planId);

    String getUserName();
    void setUserName(String userName);

    String getLastMergeShah();
    void setLastMergeSash(String mergeShah);

    String getBody();
    void setBody(String body);
}
