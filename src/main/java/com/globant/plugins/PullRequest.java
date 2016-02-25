package com.globant.plugins;

import net.java.ao.Entity;
import net.java.ao.Preload;


/**
 * Created by claudio.melendrez on 22/02/16.
 *
 * Stores a pull request key information.
 */
@Preload
public interface PullRequest extends Entity {

    int getPrNumber();
    void setPrNumber(int prNumber);

    String getBaseBranch();
    void setBaseBranch(String baseBranch);

    String getHeadBranch();
    void setHeadBranch(String headBranch);

    String getLastMergeShah();
    void setLastMergeSash(String mergeShah);

    String getBody();
    void setBody(String body);
}
