import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {
  createBrowserRouter,
  RouterProvider,
} from 'react-router-dom';
import {
  NotFound,
  MainPage,
  SignUp,
  SignIn,
  Problems,
  Solutions,
  SolutionDetail,
  Profile,
  MyPage,
  Account,
  MySolution,
  MyComment,
  Like,
  Follow,
} from './pages';
import { 
  AddSolution,
  EditSolution,
  Scrap,
  WriteSolution
} from './pages/BoardPage';
import { KakaoRedirect } from './components/SignUp/KakaoRedirect';
import { NaverRedirect } from './components/SignUp/NaverRedirect';
import { GoogleRedirect } from './components/SignUp/GoogleRedirect';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    errorElement: <NotFound />,
    children: [
      { index: true, path: '/', element: <MainPage /> },
      { path: 'signUp', element: <SignUp /> },
      { path: 'login', element: <SignIn /> },
      { path: 'login/kakao', element: <KakaoRedirect /> },
      { path: 'login/naver', element: <NaverRedirect /> },
      { path: 'login/google', element: <GoogleRedirect /> },
      { path: 'problems', element: <Problems /> },
      {
        path: 'solutions/:problemId',
        element: <Solutions />,
      },
      { path: 'solution/:solutionId/detail',element: <SolutionDetail /> },
      { path : 'solution/:solutionId/detail/scrap', element: <Scrap />},
      {
        path: 'registerReview',
        element: <WriteSolution />,
      },
      { path: 'profile/:userId', element: <Profile /> },
      { path: 'myPage', element: <MyPage /> },
      { path: 'account', element: <Account /> },
      { path: 'mySolution', element: <MySolution /> },
      { path: 'myComment', element: <MyComment /> },
      { path: 'like', element: <Like /> },
      { path: 'follow', element: <Follow /> },
      { path: 'myPage/addsolution', element: <AddSolution/> },
      { path: 'myPage/solutions/:solutionId/editsolution', element: <EditSolution/> },
    ],
  },
]);

const root = ReactDOM.createRoot(
  document.getElementById('root')
);
root.render(<RouterProvider router={router} />);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
