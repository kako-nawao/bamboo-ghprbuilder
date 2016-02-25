package com.globant.plugins;

import com.atlassian.activeobjects.external.ActiveObjects;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;


public class PullRequestServiceImpl implements PullRequestService
{
    private final ActiveObjects ao;

    public PullRequestServiceImpl(ActiveObjects ao)
    {
        this.ao = checkNotNull(ao);
    }

    @Override
    public PullRequest add(int prNumber, String baseBranch, String headBranch, String mergeHash, String body)
    {
        final PullRequest pr = ao.create(PullRequest.class);
        pr.setPrNumber(prNumber);
        pr.setBaseBranch(baseBranch);
        pr.setHeadBranch(headBranch);
        pr.setLastMergeSash(mergeHash);
        pr.setBody(body);
        return pr;
    }

    @Override
    public List<PullRequest> all()
    {
        return newArrayList(ao.find(PullRequest.class));
    }
}
