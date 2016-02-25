package com.globant.plugins;

import java.util.List;
import com.atlassian.activeobjects.tx.Transactional;


/**
 * Created by claudio.melendrez on 22/02/16.
 *
 * AO Service for PullRequest objects.
 */
@Transactional
public interface PullRequestService {

    PullRequest add(int number, String baseBranch, String headBranch, String mergeHash, String body);

    List<PullRequest> all();
}
