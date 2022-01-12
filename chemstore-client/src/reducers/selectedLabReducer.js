import { SELECT_LAB } from "../actions/types";

const initialState = false

export default function selectedLabReducer(state = initialState, action) {
    switch (action.type) {
        case SELECT_LAB:
            return  action.payload
        default: 
            return state
    }
}