package com.kyj.fmk.core.service.cmcd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyj.fmk.core.exception.custom.KyjBizException;
import com.kyj.fmk.core.exception.custom.KyjSysException;
import com.kyj.fmk.core.model.CmCdConst;
import com.kyj.fmk.core.model.cmcd.req.ReqCommonCdDTO;
import com.kyj.fmk.core.model.cmcd.req.ReqSkillCdDTO;
import com.kyj.fmk.core.model.cmcd.res.ResCommonCdDTO;
import com.kyj.fmk.core.model.cmcd.res.ResSkillCdDTO;
import com.kyj.fmk.core.model.enm.CmErrCode;
import com.kyj.fmk.core.redis.RedisKey;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2025-08-111
 * @author 김용준
 * Restful Api에서 사용하는 공통코드에 대한 레디스조회를 위한 서비스다
 *
 */
@Service
@RequiredArgsConstructor
public class CmCdRedisServiceImpl implements CmCdRedisService{

    private final RedisTemplate<String,String> redisTemplate;

    private final ObjectMapper objectMapper;
    /**
     * 공통코드를 레디스에서 조회하는 서비스
     * @param reqCommonCdDTO
     * @return
     */
    @Override
    public  Map<String, String> selectRedisCmCdMap(ReqCommonCdDTO reqCommonCdDTO) {

        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        Map<String, String> grpStCdMap =  null;


//커스터 마이징 부분
//        if(reqCommonCdDTO.getCmCd().equals(CmCdConst.TEAM_STY_CD)){
//            grpStCdMap = hashOps.entries(RedisKey.CM_TEAM_STY_CD);
//
//
//        } else if (reqCommonCdDTO.getCmCd().equals(CmCdConst.MT_STY_CD)) {
//            grpStCdMap = hashOps.entries(RedisKey.CM_MT_STY_CD);
//
//
//        } else if (reqCommonCdDTO.getCmCd().equals(CmCdConst.RECRUIT_ST_CD)) {
//            grpStCdMap = hashOps.entries(RedisKey.CM_RECRUIT_ST_CD);
//
//
//        } else if (reqCommonCdDTO.getCmCd().equals(CmCdConst.GRP_ST_CD)) {
//            grpStCdMap = hashOps.entries(RedisKey.CM_GRP_ST_CD);
//
//
//        } else if (reqCommonCdDTO.getCmCd().equals(CmCdConst.CMC_TONE_CD)) {
//            grpStCdMap = hashOps.entries(RedisKey.CM_CMC_TONE_CD);
//
//
//        } else if (reqCommonCdDTO.getCmCd().equals(CmCdConst.APY_ST_CD)) {
//            grpStCdMap = hashOps.entries(RedisKey.CM_APY_ST_CD);
//
//
//        }

        return grpStCdMap;
    }





}
