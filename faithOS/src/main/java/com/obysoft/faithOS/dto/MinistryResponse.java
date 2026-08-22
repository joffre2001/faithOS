package com.obysoft.faithOS.dto;
import java.util.List;
public record MinistryResponse(Long id,String name,String description,Long leaderId,String leaderName,List<MinistryMemberResponse> members,Boolean active){}
