import { css } from "@emotion/react";

export const MainBody = css`
  width: 1200px;
  height: 990px;
  margin: 0 auto;
  display: flex; 
  `;

export const LeftBody = css`
  width: 490px;
  height: 990px;
  flex-direction: column;
  margin-left: 73px;
  margin-top: 50px;
  align-items: center;
`;

export const RightBody = css`
  // width: 490px; 사이드바 사이즈 바뀜으로 주석처리
  height: 990px;
  flex-direction: column;
  margin-top: 50px;
  align-items: center;
 `;