package io.mkeasy.webapp.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

// import kr.or.voj.webapp.controller.AutoController;
import io.mkeasy.webapp.utils.RSMeta;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

@Slf4j
@Service
public class MyBatisProcessor implements ProcessorService{
	public static String PATH_SYSTEM = "system";
	public static String PATH_MAPPER = "mapper";
	private Map<String, List<MappedStatementInfo>> mappedStatementInfoMap = null;
	private SqlSession sqlSession;
	
	public Object execute(ProcessorParam processorParam) throws Exception {
		String path = processorParam.getQueryPath();
		CaseInsensitiveMap params = processorParam.getParams();
		String action = processorParam.getAction();
		ServletRequest request = processorParam.getRequest();
		Map<String, Object> resultSet = new LinkedCaseInsensitiveMap<Object>();
		Map<String, Map<String, RSMeta>> resultMeta = new HashMap<String, Map<String,RSMeta>>();
		log.info("MyBatisProcessor");
		List<MappedStatementInfo> msList = getList(path, action);
		
		for(MappedStatementInfo msi : msList){
			Object result = null;
			if (msi.isSelect) {
				 List list = sqlSession.selectList(msi.id, params);
				 Map<String, RSMeta> rSMeta = ProcessorServiceFactory.getRsMeta(msi.id+"-Inline"); 
				 
				 resultMeta.put(msi.returnId , rSMeta);
				//	sqlSession.getConfiguration().getMappedStatement(msi.id).getResultMaps()
				// sqlSession.
				// Connection con = sqlSession.getConnection();
				// con.
				 if(msi.isSingleRow){
					 result = list.size()>0 ? list.get(0) : new HashMap();
				 }else{
					 result = list;
				 }
			}else{
				result = sqlSession.update(msi.id, params);
			}
			//쿼리결과 리턴값으로 설정
			resultSet.put(msi.returnId, result);
			//컬럼정보를 request에 설정한다.
			if(request!=null){
				request.setAttribute("__META__", resultMeta);
			}
			//결과를 쿼리의 인자로 설정한다.
			params.put(msi.returnId, result);
		}

		return resultSet;
	}

	class MappedStatementInfo{
		boolean isSingleRow = false;
		String id;
		String returnId;
		boolean isSelect = false;
		
		public MappedStatementInfo(String id, String returnId, boolean isSelect, boolean isSingleRow){
			this.isSingleRow = isSingleRow;
			this.id = id;
			this.returnId = returnId;
			this.isSelect = isSelect;
			
		}
	}
	
	public List<MappedStatementInfo> getList(String path, String action) {
		String key = path + "." + action;
		
		if(mappedStatementInfoMap!=null){
			return mappedStatementInfoMap.get(key);
		}
		
		if(sqlSession==null){
			sqlSession = (SqlSession)ProcessorServiceFactory.getBean(SqlSession.class);
			//if(sqlSession==null){
			//	sqlSession = ((SqlSessionFactoryBean)ProcessorServiceFactory.getBean(SqlSessionFactoryBean.class)).get;
			//}
		}
		List<String> idList = new ArrayList<String>();
		Map<String, List<MappedStatementInfo>> msInfoMap = new LinkedCaseInsensitiveMap<List<MappedStatementInfo>>();
		
		Collection<String> collection = sqlSession.getConfiguration().getMappedStatementNames();
	
		
		for(String id : collection){
			idList.add(id);
		}
		
		String[] ids =  idList.toArray(new String[0]);
		Arrays.sort(ids);
		
		for(String id : ids){
			boolean isSingleRow = false;
			boolean isSelect = false;
			
			String keyId = StringUtils.substringBefore(id, "_");
			if(StringUtils.isEmpty(keyId)){
				keyId = id;
			}
			String[] idL = id.split("_");
			String returnId = idL.length == 3 ? idL[2] : "";
			if(StringUtils.isEmpty(returnId)){
				returnId = StringUtils.substringAfter(id, ".");
			}			
			
			if(returnId.startsWith("#")){
				isSingleRow = true;
				returnId = returnId.substring(1);
			}
			
			MappedStatement mappedStatement = null;
			
			try{
				mappedStatement = sqlSession.getConfiguration().getMappedStatement(id);
			}catch(Exception e){
				continue;
			}
			
			if(!id.equals(mappedStatement.getId())){
				continue;
			}
			//mappedStatement.getConfiguration().
			if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT ) {
				isSelect = true;
			}
		
			MappedStatementInfo msi = new MappedStatementInfo(id, returnId, isSelect, isSingleRow);
			
			List<MappedStatementInfo> list = msInfoMap.get(keyId);
			
			if(list==null){
				list = new ArrayList<MyBatisProcessor.MappedStatementInfo>();
				msInfoMap.put(keyId, list);
			}
			
			list.add(msi);
		}
		
		mappedStatementInfoMap = msInfoMap;
		ProcessorServiceFactory.setMyBatisMappedStatementInfoMap(mappedStatementInfoMap);
		
		return mappedStatementInfoMap.get(key);
	}
}
