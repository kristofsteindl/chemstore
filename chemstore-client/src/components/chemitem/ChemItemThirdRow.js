import DuButtons from "../UI/DuButtons";

const ChemItemThirdRow = props => {
    const { chemItem, getCollapseProps, setActiveModal } = props
    const chemical = chemItem.chemical

    return (
        <div {...getCollapseProps()}>
            <div className="row content" style={{padding: "10px"}}>
                <div className="col-10" />
                    
                <div className="col-sm-2">
                    <DuButtons 
                        updateFormTo={`/update-chem-item/${chemItem.id}`}
                        onDelete={() => setActiveModal("DELETE")}
                    /> 
                </div>
            </div>
        </div>
    )
}

export default ChemItemThirdRow