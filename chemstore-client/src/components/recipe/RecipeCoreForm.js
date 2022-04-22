import axios from "axios"
import classNames from "classnames"
import { useEffect, useState } from "react"
import Select from "react-dropdown-select"
import { useSelector } from "react-redux"

const RecipeCoreForm = props => {
    const { 
        isUpdate,
        units, 
        errors,
        selectedProject, setSelectedProject,
        name, setName,
        amount, setAmount,
        unit, setUnit,
        shelfLifeInDays, setShelfLifeInDays
    } = props 
    const [projects, setProjects] = useState([])

    const selectedLab = useSelector((state) => state.selectedLab)


    useEffect(() => {
        axios.get(`/api/logged-in/project/${selectedLab.key}`).then(result => {setProjects(result.data)})
    }, [])

    return (
        <div>
            <div className="form-group row mb-3">
                <label htmlFor="project" className="col-sm-2 col-form-label">project</label>
                <div className="col-sm-10">
                    <Select
                        disabled={isUpdate}
                        options={projects}
                        values={projects.filter(project => selectedProject && (project.id === selectedProject.id))}
                        labelField="name"
                        valueField="name"
                        placeholder="project"
                        searchable={false}
                        clearable={false}
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={items => setSelectedProject(items[0] ? items[0] : "")}
                    />
                </div>
            </div>
            <div className="form-group row mb-3">
                <label htmlFor="name" className="col-sm-2 col-form-label">recipe name</label>
                <div className="col-sm-10">
                    <input 
                        name="name"
                        value={name}
                        onChange={event => setName(event.target.value)}
                        type="text" 
                        className={classNames("form-control form-control-lg", {"is-invalid": errors.name})} 
                        placeholder="name" 
                    />
                    {
                        (errors.name && <div className="text-danger">{errors.name}</div>)
                    }
                    
                </div>
            </div>
            <div className="form-group row mb-3">
                <label htmlFor="amount" className="col-sm-2 col-form-label">amount</label>
                <div className="col-sm-10">
                    <input 
                        name="amount"
                        value={amount}
                        onChange={event => setAmount(parseFloat(event.target.value))}
                        type="number" 
                        className={classNames("form-control form-control-lg", {"is-invalid": errors.amount})} 
                        placeholder="amount" 
                        min="0.000"
                        step="0.001"
                    />
                    {
                        (errors.amount && <div className="text-danger">{errors.amount}</div>)
                    }
                    
                </div>
            </div>
            <div className="form-group row mb-3">
                <label htmlFor="unit" className="col-sm-2 col-form-label">unit</label>
                <div className="col-sm-10">
                    <Select
                        options={units}
                        values={[unit]}
                        labelField="unit"
                        placeholder="unit"
                        valueField="unit"
                        searchable={false}
                        clearable={false}
                        style={{height: "42px", fontSize: "16px"}}
                        className={classNames("form-control form-control-lg", {"is-invalid": errors.unit})} 
                        onChange={items => setUnit(items[0] ? {unit: items[0].unit} : "")}
                    />
                    {
                        (errors.unit && <div className="text-danger">{errors.unit}</div>)
                    }
                </div>
            </div>
            <div className="form-group row mb-3">
                <label htmlFor="shelfLifeInDays" className="col-sm-2 col-form-label">shelf life (days)</label>
                <div className="col-sm-10">
                    <input 
                        name="shelfLifeInDays"
                        value={shelfLifeInDays}
                        onChange={event => setShelfLifeInDays(event.target.value)}
                        type="number" 
                        className={classNames("form-control form-control-lg", {"is-invalid": errors.shelfLifeInDays})} 
                        placeholder="shelf life (days)" 
                        min="0"
                    />
                    {
                        (errors.shelfLifeInDays && <div className="text-danger">{errors.shelfLifeInDays}</div>)
                    }
                </div>
            </div>
        </div>
    )
}

export default RecipeCoreForm