

//video.js 예제

/*  
 *  setVideo : 비디어 플레이어 설정
 *   - playerId : <video> 태그 id
 *   - src : 동영상 경로
 *   - poster : 동영상 썸네일 경로
 */
function setVideo(playerId,src){
	
	/* video.js 
	*  source : 동영상 경로
	*  playsinline : 모바일 브라우저 환경의 경우 동영상 재생 시 모바일 내장 플레이어로 실행이 되기 때문에 웹페이지에서 인라인형태로 제공하고 싶은 경우 true로 설정
	*  muted : 최초 음량 무음상태 설정 여부
	*    - ture : 무음
	*  preload : 동영상 경로를 재생 버튼을 클릭 시 영상을 다운로드 받는 지 재생 전에 미리 일부분을 다운로드 받아 로딩이 없이 제공받을 지 여부를 설정
	*    - metadata : 기본정보만 미리 load
	*    - auto : 컨텐츠를 미리 로드하여 로딩이 없이 동영상을 시작
	*  playbackRates : 배속 기능
	*  controls : 동영상 제어 기능
	*  controlBar : 제어바
	*    - 기본적으로 재생/일시정지, 음량조절, 재생바, 남은 시간, 전체화면 버튼 등으로 구성
	*    - playToggle : 재생/일시정지
	*    - pictureInPictureToggle : PIP 모드 재생
	*    - remainingTimeDisplay : 남은 시간
	*    - progressControl : 재생바
	*/
	var options = {
            sources : [
                { src : src, type : "video/mp4"}
            ],
            playsinline : true,
            muted : true,
            preload : "metadata",
            playbackRates: [0.5, 1, 1.5, 2],
            controls : true,
            controlBar : {
                playToggle : true,
                pictureInPictureToggle : true,
                remainingTimeDisplay : true,
                progressControl : false,
            }
	}
	var player = videojs(playerId, options);
	
	return player;
}

/*  
 *  playVideo : 비디오 플레이어 설정
 *   - player : 플레이어 
 *   - playTime : 시청 시간 
 */
function playVideo(){
	
	var player = arguments[0];
	var playTime = arguments[1];
	
	//playTime값이 넘어오고 + 시청내역이 있으면
	if(arguments[1] != undefined && playTime > 0){
		if(confirm("기존 시청내역이 존재합니다. 이어보시겠습니까?")){
			player.currentTime(playTime);
		}
	}

}

/*  
 *  closeVideo : 동영상 닫기
 *   - player : 플레이어
 *   - playTime : 시청 시간 
 *   - timer : 타이머 
 */
function closeVideo() {
	
	var player = arguments[0];
	var playerId = arguments[1];
	var playTime = arguments[2];
	var timer = arguments[3];
	
	player.pause();
	playTime = player.currentTime()
	
	player = videojs(playerId, {});
	player.dispose();
	
	//timer 값이 넘어오면
	if(arguments[2]){
		clearInterval(timer);
	}
	
	return playTime;
}
