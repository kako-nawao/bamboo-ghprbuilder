package com.globant.plugins;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.bamboo.plugins.git.GitHubRepository;
import com.atlassian.bamboo.user.BambooUserManager;
import com.atlassian.bamboo.plan.cache.ImmutableChain;
import com.atlassian.bamboo.plan.ExecutionRequestResult;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanExecutionManager;
import com.atlassian.bamboo.plan.PlanHelper;
import com.atlassian.bamboo.plan.PlanResultKey;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.user.User;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by claudio.melendrez on 22/02/16.
 *
 * Servlet to trigger builds from posted PR data from github.
 */
public class PRBuilderServlet extends HttpServlet
{
    // Logger and json parser
    private static final Logger log = LoggerFactory.getLogger(PRBuilderServlet.class);
    private static final JSONParser jsonParser = new JSONParser();

    // Services
    private final PRBuilderConfigService prbcService;
    private final PlanManager planManager;
    private final PlanExecutionManager planExecutionManager;
    private final BambooUserManager userManager;

    public PRBuilderServlet(PRBuilderConfigService prbcService, PlanManager planManager, PlanExecutionManager planExecutionManager, BambooUserManager userManager)
    {
        this.prbcService = checkNotNull(prbcService);
        this.planManager = checkNotNull(planManager);
        this.planExecutionManager = checkNotNull(planExecutionManager);
        this.userManager = checkNotNull(userManager);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.getWriter().write("lalalalala");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // Always return JSON
        resp.setContentType("text/javascript");

        // Build string of posted data from webhook
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = req.getReader().readLine()) != null) {
            sb.append(s);
        }

        // Try to queue builds
        try {
            // Get pull request data from posted data
            JSONObject postData = (JSONObject) PRBuilderServlet.jsonParser.parse(sb.toString());
            JSONObject prData = (JSONObject) postData.get("pull_request");

            // Check if PR is open, if so process
            String action = (String) postData.get("action");
            String prState = (String) prData.get("state");
            if (action.equals("opened") && prState.equals("open")) {
                // Main loop: check every active PRBuilderConfig for match
                for (PRBuilderConfig prBuilderConfig: this.prbcService.all())
                {
                    // Get plan and maybe build it
                    Plan plan = planManager.getPlanById(prBuilderConfig.getPlanId());
                    if (plan != null && !plan.isSuspendedFromBuilding())
                    {
                        GitHubRepository repo = (GitHubRepository) PlanHelper.getDefaultRepository(plan);
                        if (repo != null)
                        {
                            boolean shouldBuild = this.shouldBuildPR(repo, prData);
                            if (shouldBuild)
                            {
                                PlanResultKey prk = this.queueBuild(plan, repo, prData, prBuilderConfig.getUserName());
                                resp.getWriter().write(prk.toString());
                                return;

                            }
                        }
                    }
                }
            }
        }
        catch (ParseException e) {
            throw new ServletException("Weird response, could not be parsed!");
        }
        resp.getWriter().write("Not building\n");
    }

    private boolean shouldBuildPR(GitHubRepository repo, JSONObject prData)
    {
        // Get repo data to compare
        String planRepoName = repo.getRepository();
        String planBranch = repo.getVcsBranch().getName();

        // Get base branch data
        // Note: we care about the merge target (base), not the source (head) now
        JSONObject baseBranch = (JSONObject) prData.get("base");
        JSONObject repoData = (JSONObject) baseBranch.get("repo");
        String baseRef = (String) baseBranch.get("ref");
        String pullRepoName = (String) repoData.get("full_name");

        // Should build if url and branch match
        return planRepoName.equals(pullRepoName) && baseRef.equals(planBranch);
    }

    private PlanResultKey queueBuild(Plan plan, GitHubRepository repo, JSONObject prData, String userName)
    {
        // Get PR head revision
        JSONObject headBranch = (JSONObject) prData.get("head");
        String rev = (String) headBranch.get("sha");

        // Get user and build immuetable chain from plan
        User user = userManager.getUser(userName);
        ImmutableChain chain = (ImmutableChain) plan;

        // Build params and vars for plan
        Map<String,String> params = new HashMap<String, String>();
        params.put("customRevision", rev);
        Map<String,String> vars = new HashMap<String, String>();

        // Start manual execution of plan and return result key
        ExecutionRequestResult err = planExecutionManager.startManualExecution(chain, user, params, vars);
        return err.getPlanResultKey();
    }
}