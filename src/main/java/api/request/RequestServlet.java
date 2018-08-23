package api.request;

import format.RespWrapper;
import util.DBUtil;
import util.RequestParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "RequestServlet", urlPatterns = {"/api/datacrawling/request/*"})
public class RequestServlet extends HttpServlet {

    /*
    for api :/api/datacrawling/request/all
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] pathParam=RequestParser.parsePath(request.getRequestURI(),2);
        if("request".equals(pathParam[0])&&"all".equals(pathParam[1])){
            String[] params = {"requestID", "requestName", "requestDesc","createdTime"};
            String[][] requesttable = DBUtil.select("requesttable", params);
            int total = requesttable.length;
            List<Map<String,Object>> strings = new ArrayList<>();
            for (int i = 0; i < requesttable.length; i++) {
                Map<String,Object> data=new HashMap<>();
                for (int j = 0; j < requesttable[0].length; j++) {
                    data.put(params[j], requesttable[i][j]);
                }
                strings.add(data);
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(RespWrapper.build(strings, total));
        }else {
            response.getWriter().println(RespWrapper.build(RespWrapper.AnsMode.SYSERROR,null));
        }
    }
    /*
        for api :/api/datacrawling/request/:id
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String[] pathParam=RequestParser.parsePath(request.getRequestURI(),2);
        int requestID=0;
        try{
            requestID=Integer.parseInt(pathParam[1]);
        }catch (NumberFormatException e){
            response.getWriter().println(RespWrapper.build(RespWrapper.AnsMode.SYSERROR,null));
            return;
        }
        request.setCharacterEncoding("UTF-8");
        String requestName = request.getParameter("requestName");
        String requestDesc = request.getParameter("requestDesc");
        String[] params = {"requestName", "requestDesc",};
        String[] params_value = {requestName, requestDesc};
        boolean isUpdated = DBUtil.update("requesttable", params, params_value, requestID);
        System.out.println("isUpdated ?:" + isUpdated);
        Map<String, Object> data = new HashMap<>();
        data.put("requestID", requestID);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(RespWrapper.build(data));
    }
    /*
        for api :/api/datacrawling/request/new
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        //前端页面保证这些值均不为空，这里无需验证
        String[] pathParam=RequestParser.parsePath(request.getRequestURI(),2);
        if("request".equals(pathParam[0])&&"new".equals(pathParam[1])){
            String taskName = request.getParameter("requestName");
            String runningMode = request.getParameter("requestDesc");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String createdTime=df.format(new Date()).toString();
            //处理数据插入
            String[] params = {"requestName", "requestDesc", "createdTime"};
            String[] con_values = {taskName, runningMode, createdTime};
            boolean isInserted = DBUtil.insert("requesttable", params, con_values);
            int requestID = DBUtil.getLasttaskID();
            Map<String, Object> data = new HashMap<>();
            data.put("requestID", requestID);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(RespWrapper.build(data));
        }else {
            response.getWriter().println(RespWrapper.build(RespWrapper.AnsMode.SYSERROR,null));
        }

    }

}