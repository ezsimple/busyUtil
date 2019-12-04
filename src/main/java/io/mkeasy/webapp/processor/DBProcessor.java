package io.mkeasy.webapp.processor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import io.mkeasy.webapp.db.DefaultDaoSupportor;
import io.mkeasy.webapp.db.QueryInfoFactory;
import net.sf.json.JSONObject;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


/**
 * <pre>
 * 작 성 자 : 석승한
 * 작 성 일 : 2014. 3. 18
 * 설    명 : 

	id: 반환될 결과의 변수명
	action: 쿼리중 실행될 그룹명 (action이 같은 쿼리들을 순차적으로 실행한다.)
	singleRow: 한개의 레코드만 반환한다.
	loop: 입력될 레코드가 복수개 인 경우 입력된 필드명을 기준으로 필드 갯수만큼 반복 실행한다.
	
	예제)
 	\* {
 		id:'addAtt', action:'u', loop: 'attach'
	} *\
		UPDATE sys_notice_m
		SET use_yn = 'N'
		WHERE notice_id = :notice_id;
	\* {
		id:'row', action:'d',  singleRow="true", desc:"데이타소스를 설정해야 하는 경우 'ds:dsname'와 같이 설정할수 있다." }
	*\
		SELECT *
		FROM sys_notice_m
		SET use_yn = 'N'
		WHERE notice_id = :notice_id;

 * 
 * </pre>
 */
@Service
public class DBProcessor implements ProcessorService{
	public Object execute(ProcessorParam processorParam) throws Exception {
		String path = processorParam.getQueryPath();
		CaseInsensitiveMap params = processorParam.getParams();
		String action = processorParam.getAction();
		ServletRequest request = processorParam.getRequest();
		
		Map<String, Object> resultSet = new HashMap<String, Object>();
		//쿼리정보를 가지고 온다.
		Map<String, JSONObject> queryInfos = QueryInfoFactory.findQuerys(path);
		
		if(queryInfos==null){
			return resultSet;
		}
		DefaultDaoSupportor defaultDaoSupportor = ProcessorServiceFactory.getDaoSupportor(null);
		DefaultDaoSupportor daoSupportor = null;
		//쿼리 목록에서 조건에 부합하는 쿼리를 순차적으로 실행한다.
		for(String key : queryInfos.keySet()){
			JSONObject queryInfo = queryInfos.get(key);
			Map<String, String> queryAction = (Map<String, String>)queryInfo.get("action");
			//쿼리에 액션이 존재하는 경우만 체크하고 없는 경우는 무조건 실행
			if(queryAction!=null){
				if(!queryAction.containsKey(action)){//같은 액션만 실행
					continue;
				}
			}

			if(QueryInfoFactory.getBoolean("subQuery", queryInfo, false)){
				continue;
			}
			//접속할 데이타 소스를 가지고 온다.
			String ds = QueryInfoFactory.getString("ds", queryInfo, "");
			daoSupportor = StringUtils.isNotEmpty(ds) ? ProcessorServiceFactory.getDaoSupportor(ds) : defaultDaoSupportor;				
			
			if(daoSupportor==null){
				throw new RuntimeException(ds + "로 정의된 데이타 소스가 존재하지 않습니다.");
			}
			
			String query = queryInfo.getString("query");
			query = QueryInfoFactory.makeQuery(key, query, queryInfos);
			boolean isSingleRow = QueryInfoFactory.getBoolean("singleRow", queryInfo, false);
			String id = queryInfo.getString("id");
			String loop = QueryInfoFactory.getString("loop", queryInfo, "");
			//반복실행할 커리에 대한 처리
			if(request!=null && !"".equals(loop)){
				Map<String, String[]> reqParamMap = request.getParameterMap();
				String[] vals = reqParamMap.get(loop);
				if(vals!=null){
					//loop필드 값의 갯수 만큼 반복실행한다.
					for(int i=0; i<vals.length; i++){
						setRequestValue(i, params, reqParamMap);//해당 인덱스의 request값을 파라메터값에 설정한다.
						daoSupportor.executeQuery(id, query, isSingleRow, params, resultSet);
					}
				}
			}else{
				daoSupportor.executeQuery(id, query, isSingleRow, params, resultSet);
			}
		}
		
		return resultSet;
	}



	/**
	 * 반복실행 할 커리에 대한 처리 Request정보에서 해당 인텍스 데이타를 현재정보로 설정해준다
	 * @param i
	 * @param params
	 * @param reqParamMap
	 * @return
	 */
	private CaseInsensitiveMap setRequestValue(int i, CaseInsensitiveMap params, Map<String, String[]> reqParamMap) {
		for(String ctl : reqParamMap.keySet()){
			String[] pvs = reqParamMap.get(ctl);
			String pv = pvs[i<pvs.length ? i: (pvs.length-1)];
			params.put(ctl, pv);
		}
		return params;
	}
}
