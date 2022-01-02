import { SELECT_LAB } from "../actions/types";

const initialState = {
    selectedLab: {}
}

export default function selectedLabReducer(state = initialState, action) {
    switch (action.type) {
        case SELECT_LAB:
            return {
                ...state,
                selectedLab: action.payload
            }
        default: 
            return state
    }
}