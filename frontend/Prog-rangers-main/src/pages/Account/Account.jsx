import React, { useEffect, useState } from 'react';
import { css } from '@emotion/react';
import { theme } from '../../components/Header/theme';
import { Link } from 'react-router-dom';
import { SideBar } from '../../components/SideBar/SideBar';
import { Button } from '@mui/material';
import { profileContentStyle, editBtnStyle, deleteBtnStyle } from './AccountStyle';
import axios from 'axios';

export const Account = () => {
  const [userData, setUserData] = useState({
    nickname: '',
    email: '',
    github: '',
    introduction: '',
    passwordModifiedAt: '',
    photo: '',
  });

  // 유저 정보 조회 api
  useEffect(() => {
    const token = localStorage.getItem('token');    
    fetch("http://13.124.131.171:8080/api/v1/mypage/account-settings", {
      method: "GET",
      headers: {Authorization: `Bearer ${token}`},
    })
    .then((response) => response.json())
    .then((json) => {
      setUserData({
        nickname: json.nickname || '',
        email: json.email || '',
        github: json.github || '',
        introduction: json.introduction || '',
        passwordModifiedAt: json.passwordModifiedAt || '',
        password: json.password || '',
        photo: json.photo || '',
      })
    })
    .catch((error) => {
      console.error(error);
    })
  }, []);

  // 날짜 형식 변환
  const formattedDate = new Date(userData.passwordModifiedAt);

  const year = formattedDate.getFullYear();
  const month = formattedDate.getMonth() + 1;
  const day = formattedDate.getDate();
  const hours = formattedDate.getHours();
  const minutes = formattedDate.getMinutes();
  const seconds = formattedDate.getSeconds();

  const formattedHours = hours < 10 ? `0${hours}` : hours;
  const formattedMinutes = minutes < 10 ? `0${minutes}` : minutes;
  const formattedSeconds = seconds < 10 ? `0${seconds}` : seconds;

  const formattedTime = `${year}년 ${month}월 ${day}일 ${formattedHours}:${formattedMinutes}:${formattedSeconds}`;


  return (
    <div 
      className='container' 
      css={css`
      width: 1200px;
      height: 100%;
      display: flex;
      justify-content: space-between;
      margin: 0 auto;
    ` }
    >
      <SideBar />
      <div
        className='content'
        css={css`
        width: 800px;
        margin: 100px 110px 0;
        `}
        >

          <div 
            className='profile'
            css={css`
            height: 330px;
            display: flex;
            justify-content: space-between;
            `}>
              <div css={css`
              width: 250px;
              display: flex;
              flex-direction: column;
              align-items: center;
              `}>
                <img
                  src={userData.photo ? userData.photo : 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png'}                  
                  alt='profileImg'
                  width='250px'
                  css={css`
                  border-radius: 50%;
                  `}></img>
                  <Link to='/accountChange'
                  css={editBtnStyle}>수정하기</Link>
              </div>

              <div css={css`
              width: 500px;
              font-size: 18px
              `}>
                <div className='nickname' css={profileContentStyle}>
                  <div css={css`width: 72px`}>닉네임</div>
                  <div>{userData.nickname}</div>
                </div>
                <div className='email' css={profileContentStyle}>
                  <div css={css`width: 72px`}>이메일</div>
                  <div>{userData.email}</div>
                </div>
                <div className='github' css={profileContentStyle}>
                 <div css={css`width: 72px`}>깃허브</div>
                 <div>{userData.github}</div>
                </div>
                <div className='introduce' css={profileContentStyle}>
                 <div css={css`width: 72px`}>소개</div>
                 <div>{userData.introduction}</div>
                </div>
                <div className='pw' css={profileContentStyle}>
                 <div css={css`width: 72px`}>비밀번호</div>
                 <div>최근 변경일: {formattedTime}</div>
                </div>
              </div>
            
          </div>

          <div 
            className='accountDelete'
            css={css`
            height: 100px;
            margin-top: 132px;
            `}>
            <div css={css`
            font-size: 20px;
            font-weight: 700;
            padding: 0 0 10px 30px;
            `}>계정삭제</div>

            <div css={css`
            height: 60px;
            padding: 0 10px 0 30px;

            display: flex;
            justify-content: space-between;
            align-items: center; 

            border: 1px solid ${theme.colors.light1};
            border-radius: 30px;
            `}
          >
            <input
              type="text"
              placeholder="계정 삭제시 작성한 리뷰가 전부 사라집니다."
              css={css`
                width: 100%;
                font-size: 16px;
                outline: none;
                border: none;
              `}
            />
            <button
              type="button"
              css={deleteBtnStyle}>
              삭제하기
            </button>
          </div>
          </div>
        
      </div>
    </div>
  );
}