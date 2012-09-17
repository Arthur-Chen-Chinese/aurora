package aurora.presentation.component.std;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.application.AuroraApplication;
import aurora.presentation.BuildSession;
import aurora.presentation.IViewBuilder;
import aurora.presentation.ViewContext;
import aurora.presentation.ViewCreationException;
import aurora.presentation.component.std.config.ComponentConfig;
import aurora.presentation.component.std.config.TextFieldConfig;

public class QueryForm extends Component implements IViewBuilder, ISingleton {
	private static final String DEFAULT_TABLE_CLASS = "layout-table";
	private static final String DEFAULT_WRAP_CLASS = "form_body_wrap";
	private static final String DEFAULT_HEAD_CLASS = "form_head";
	private static final String FORM_TOOL_BAR = "formToolBar";
	private static final String FORM_BODY = "formBody";
	
	private static final String PROPERTITY_EXPAND = "expand";
	private static final String PROPERTITY_TITLE = "title";
	private static final String PROPERTITY_DEFAULT_QUERY_DATASET = "defaultquerydataset";
	private static final String PROPERTITY_DEFAULT_QUERY_FIELD = "defaultqueryfield";
	private static final String PROPERTITY_DEFAULT_QUERY_HINT = "defaultqueryhint";
	private static final String PROPERTITY_DEFAULT_QUERY_PROMPT = "defaultqueryprompt";
	
	protected int getDefaultWidth(){
		return 0;
	}
	
	protected int getDefaultHeight(){
		return 0;
	}
	
	public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
		CompositeMap view = view_context.getView();
		CompositeMap model = view_context.getModel();
		Map map = view_context.getMap();
		
		/** ID属性 **/
		String id = view.getString(ComponentConfig.PROPERTITY_ID, "");
		if("".equals(id)) {
			id= IDGenerator.getInstance().generate();
		}
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		boolean open = view.getBoolean(PROPERTITY_EXPAND, false);
		String ds = view.getString(PROPERTITY_DEFAULT_QUERY_DATASET);
		String field = view.getString(PROPERTITY_DEFAULT_QUERY_FIELD);
		String hint = view.getString(PROPERTITY_DEFAULT_QUERY_HINT);
		String queryPrompt = view.getString(PROPERTITY_DEFAULT_QUERY_PROMPT);
		int width = getComponentWidth(model, view, map).intValue();
		int height = getComponentHeight(model, view, map).intValue();
		
		String className = DEFAULT_TABLE_CLASS + " layout-form layout-title " + cls;
		String title = session.getLocalizedPrompt(view.getString(PROPERTITY_TITLE, ""));
		
		Writer out = session.getWriter();
		try{
			out.write("<table cellspacing='0' cellpadding='0' class='"+className+"' id='"+id+"'");
			if(width != 0) style ="width:" + width+"px;" + style;
			if(!"".equals(style)) {
				out.write(" style='"+style+"'");
			}
			out.write(">");
			CompositeMap formHead = view.getChild(FORM_TOOL_BAR);
			out.write("<thead>");
			if(!"".equals(title)) {
				out.write("<tr><th class='"+DEFAULT_HEAD_CLASS+"'>");
				out.write(title);
				out.write("</th></tr>");
			}
			out.write("<tr><th>");
			if(null!=formHead){
				Iterator it = formHead.getChildIterator();
				if(null !=it){
					formHead.setName("hBox");
					formHead.putBoolean(GridLayout.PROPERTITY_WRAPPER_ADJUST, true);
					CompositeMap btn = formHead.getChild("expandButton");
					if(null != btn){
						btn.setName("button");
						btn.putString("click", "function(){$('"+id+"').trigger()}");
					}
					if(null != ds && null != field){
						CompositeMap tf = new CompositeMap("textField");
						tf.setNameSpaceURI(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE);
						tf.putString(ComponentConfig.PROPERTITY_BINDTARGET, ds);
						tf.putString(ComponentConfig.PROPERTITY_NAME, field);
						tf.putString(ComponentConfig.PROPERTITY_STYLE, "width:100%");
						if(null!=hint){
							tf.putString(TextFieldConfig.PROPERTITY_EMPTYTEXT, hint);
						}
						if(null!=queryPrompt){
							tf.putString(ComponentConfig.PROPERTITY_PROMPT, session.getLocalizedPrompt(queryPrompt));
						}
						formHead.addChild(0, tf);
					}
					session.buildView(model, formHead);
				}
			}
			out.write("</th></tr></thead>");
			CompositeMap formBody = view.getChild(FORM_BODY);
			out.write("<tbody><tr><td>");
			out.write("<div class='"+DEFAULT_WRAP_CLASS+"'");
			if(!open) {
				out.write(" style='height:0'");
			}
			out.write(">");
			if(null!=formBody){
				Iterator it = formBody.getChildIterator();
				if(null !=it){
					formBody.setName("box");
					if(height != 0)formBody.put(ComponentConfig.PROPERTITY_HEIGHT, height - 26);
					session.buildView(model, formBody);
				}
			}
			out.write("</div></td></tr></tbody>");
			out.write("</table>");
			out.write("<script>");
			out.write("new $A.QueryForm({id:'"+id+"',isopen:"+open+"})");
			out.write("</script>");
		}catch (Exception e) {
			throw new ViewCreationException(e);
		}
	}

	public String[] getBuildSteps(ViewContext context) {
		return null;
	}
}