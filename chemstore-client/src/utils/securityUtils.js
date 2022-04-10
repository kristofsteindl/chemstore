import axios from "axios";
import jwt_decode from "jwt-decode";
import store from '../store';
import { FILL_LAB_DROPDOWN, SET_CURRENT_USER } from "../actions/types";

//TODO rename to 'setTokenToHeader
export const setJwt = token => {
    if (token) {
        axios.defaults.headers.common["Authorization"] = token;
    } else {
        delete axios.defaults.headers.common["Authorization"];
    }
}

export const checkIfAdmin = (selectedLab, user) => {
  return (selectedLab.key) && 
                  (user.labsAsAdmin.includes(selectedLab.value) || 
                  selectedLab.labManagers.map(manager => manager.username).includes(user.username))
}


export const checkIfManager = (selectedLab, user) => {
  return (selectedLab.key) && selectedLab.labManagers.map(manager => manager.username).includes(user.username)
}


export const checkIfAccountManager = (user) => {
  return user.username && user.authorities.map(listItem => listItem.authority).includes("ACCOUNT_MANAGER")
}

export const logoutDispatch = () => dispatch => {
    localStorage.removeItem("jwt");
    setJwt(false);
    dispatch({
        type: SET_CURRENT_USER,
        payload: {}
    });
}


export const logout = () => {
    localStorage.removeItem("jwt");
    localStorage.removeItem("selectedLab");
    setJwt(false);
    store.dispatch({
        type: SET_CURRENT_USER,
        payload: {}
    });
}

export const check = () => {
  checkExpiry()
}

const checkExpiry = () => {
    const jwtToken = localStorage.jwt;
    if (jwtToken) {
      const decodedToken = jwt_decode(jwtToken);
      const currentTime = Date.now()/1000;
      if (decodedToken.exp < currentTime) {
        console.log("token expired");  
        logout();
        window.location.replace("/");
      } 
    }
}

export const refreshState = () => {
    const jwtToken = localStorage.jwt;
    if (jwtToken) {
      const decodedToken = jwt_decode(jwtToken);
      const currentTime = Date.now()/1000;
      if (decodedToken.exp < currentTime) {
        console.log("token expired");  
        logout();
        window.location.replace("/");
      } else {
        setJwt(jwtToken);
        store.dispatch({
            type: SET_CURRENT_USER,
            payload: decodedToken
        });
        fetchLabs()
      }
    }
}

export const fetchLabs = async () => {
  try {
    const res = await axios.get('/api/logged-in/lab?onlyAvailable=true')
    store.dispatch({
        type: FILL_LAB_DROPDOWN,
        payload: res.data
    });
  } catch (error) {
    console.log(error)
  }

}


