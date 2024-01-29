import { Button } from "@mui/material";
import { isExpired } from "../../utils/chem-item-utils";

const ChemItemFirstRow = props => {
    const { chemItem, setActiveModal, getToggleProps } = props

    const chemical = chemItem.chemical


    const getOpenContent = chemItem => {
        if (chemItem.openingDate) {
            return (<div>{chemItem.openingDate}</div>)
        } else if (isExpired(chemItem)) {
            return (<div></div>)
        } else {
            return (
                <Button onClick={() => setActiveModal("OPEN")} className="btn action-button" variant="outlined" size="medium">
                    <i className="fas fa-external-link-square-alt">Open</i>
                    <i className=""></i>
                </Button>
            )
        }
    }

    const getConsumeContent = chemItem => {
        if (!chemItem.openingDate && !isExpired(chemItem)) {
            return (<div></div>)
        } else if (chemItem.openingDate && !chemItem.consumptionDate) {
            return (
                <Button className="btn action-button" color="warning" variant="outlined" size="medium" onClick={() => setActiveModal("CONSUME")}  >
                    <i className="fas fa-check-double">Consume</i>
                </Button>)
        } else {
            return (<div>{chemItem.consumptionDate}</div>)
        }
    }

    const getExpDate = chemItem => {
        if (chemItem.expirationDate) {
            return (<strong>{chemItem.expirationDate}</strong>)
        }
        return (<p>{chemItem.expirationDateBeforeOpened}</p>)
    }

    return (
        <div className="row"  {...getToggleProps()}>
            <div className="col-2">
                <h4 className="mx-auto">{chemical.shortName}</h4>
            </div>
            <div className="col-sm-1">
                <i>{chemItem.quantity} {chemItem.unit}</i>
            </div>
            <div className="col-1">
                <span className="mx-auto">{chemItem.arrivalDate}</span>
            </div>
            <div className="col-sm-2">
                <p>{chemItem.manufacturer.name}</p>
            </div>
            <div className="col-sm-2">
                <p>{chemItem.batchNumber}/{chemItem.seqNumber}</p>
            </div>
            <div className="col-sm-1">
                {getExpDate(chemItem)}
            </div>
            <div className="col-sm-1">
                {getOpenContent(chemItem)}
            </div>
            <div className="col-sm-2">
                {getConsumeContent(chemItem)}
            </div>
        </div>
    )
}

export default ChemItemFirstRow