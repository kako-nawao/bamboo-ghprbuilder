package com.globant.plugins;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.atlassian.user.User;
import com.atlassian.bamboo.user.BambooAuthenticationContext;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanHelper;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plugins.git.GitHubRepository;

import static com.google.common.base.Preconditions.*;


/**
 * Created by claudio.melendrez on 22/02/16.
 *
 * Servlet to edit PRBuilderConfig objects.
 */
public class PRBuilderConfigServlet extends HttpServlet {

    private final PRBuilderConfigService prbcService;
    private final PlanManager planManager;
    private final BambooAuthenticationContext authContext;

    public PRBuilderConfigServlet(PRBuilderConfigService prbcService, PlanManager planManager, BambooAuthenticationContext authContext)
    {
        this.prbcService = checkNotNull(prbcService);
        this.planManager = checkNotNull(planManager);
        this.authContext = checkNotNull(authContext);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        String action = req.getParameter("action");
        if (action != null && action.equals("delete"))
        {
            int prbcId = Integer.parseInt(req.getParameter("id"));
            this.delete(prbcId);
            res.sendRedirect(req.getContextPath() + "/plugins/servlet/ghprbuilder/config");
        }
        else
        {
            this.list(res);
        }
    }

    protected void delete(int prbcId)
    {
        prbcService.delete(prbcId);
    }

    protected void list(HttpServletResponse res) throws IOException, ServletException
    {
        final PrintWriter w = res.getWriter();
        w.write("<h1>Todos</h1>");

        // the form to post more TODOs
        w.write("<form method=\"post\">");
        w.write("<select name=\"plan-id\"/>");
        for (Plan plan: planManager.getAllPlansUnrestricted())
        {
            w.printf("<option value=\"%s\">%s</option>", plan.getId(), plan.getBuildName());
        }
        w.write("</select>");
        w.write("&nbsp;&nbsp;");
        w.write("<button type=\"submit\">Add</button>");
        w.write("</form>");

        w.write("<table><tbody>");
        w.write("<tr><th>Plan</th><th>Repository</th><th>Branch</th><th>User</th><th></th></tr>");

        for (PRBuilderConfig prbConfig: prbcService.all()) // (2)
        {
            int planId = prbConfig.getPlanId();
            Plan plan = planManager.getPlanById(planId);
            String repoName = "N/A";
            String branchName = "N/A";
            String buildName = "N/A";
            if (plan != null)
            {
                buildName = plan.getBuildName();
                GitHubRepository repo = (GitHubRepository) PlanHelper.getDefaultRepository(plan);
                if (repo != null)
                {
                    repoName = repo.getRepository();
                    branchName = repo.getVcsBranch().getName();
                }
            }

            w.printf("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td><a href=\"/bamboo/plugins/servlet/ghprbuilder/config?id=%s&action=delete\">x</a></td></tr>", buildName, repoName, branchName, prbConfig.getUserName(), prbConfig.getID());
        }

        w.write("</tbody></table>");
        w.write("<script language='javascript'>document.forms[0].elements[0].focus();</script>");
        w.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        final int planId = Integer.parseInt(req.getParameter("plan-id"));
        User user = authContext.getUser();
        if (user != null)
        {
            prbcService.add(planId, user.getName());
            res.sendRedirect(req.getContextPath() + "/plugins/servlet/ghprbuilder/config");
        }
        else
        {
            res.getWriter().write("Error!");
        }
    }
}
