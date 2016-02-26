package com.globant.plugins;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanHelper;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plugins.git.GitRepository;

import static com.google.common.base.Preconditions.*;


/**
 * Created by claudio.melendrez on 22/02/16.
 *
 * Servlet to edit PRBuilderConfig objects.
 */
public class PRBuilderConfigServlet extends HttpServlet {

    private final PRBuilderConfigService prbcService;
    private final PlanManager planManager;

    public PRBuilderConfigServlet(PRBuilderConfigService prbcService, PlanManager planManager)
    {
        this.prbcService = checkNotNull(prbcService);
        this.planManager = checkNotNull(planManager);
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
        w.write("<input type=\"text\" name=\"branch\"/>");
        w.write("&nbsp;&nbsp;");
        w.write("<button type=\"submit\">Add</button>");
        w.write("</form>");

        w.write("<ul>");

        for (PRBuilderConfig prbConfig: prbcService.all()) // (2)
        {
            int planId = prbConfig.getPlanId();
            Plan plan = planManager.getPlanById(planId);
            String repoName = "N/A";
            String buildName = "N/A";
            if (plan != null)
            {
                buildName = plan.getBuildName();
                GitRepository repo = (GitRepository) PlanHelper.getDefaultRepository(plan);
                repoName = (repo != null) ? repo.getLocationIdentifier() : "N/A";
            }

            w.printf("<li>%s - %s/%s<a href=\"/bamboo/plugins/servlet/ghprbuilder/config?id=%s&action=delete\">x</a></li>", buildName, repoName, prbConfig.getBranch(), prbConfig.getID());
        }

        w.write("</ul>");
        w.write("<script language='javascript'>document.forms[0].elements[0].focus();</script>");
        w.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        final int planId = Integer.parseInt(req.getParameter("plan-id"));
        final String branch = req.getParameter("branch");
        prbcService.add(planId, branch);
        res.sendRedirect(req.getContextPath() + "/plugins/servlet/ghprbuilder/config");
    }
}
