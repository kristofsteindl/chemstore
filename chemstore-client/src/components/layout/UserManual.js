import "./UserManual.css"
const UserManual = () => {
    return (
        <div className="body">
            <h1>chemstore User Manual</h1>
            <br/>
            <br/>
            <h2>Purpose</h2>
            <p>
                The purpose of this web application is to ease the burden of the registration and administration of chemicals and mixtures made from them.
                While it provides a straightforward interface, it complies the highest industrial quality assurance standards.
                It implements fine grained authorization, detailed audit logging (in progress) and every operation is highly controlled.   
             </p>
             <br/>
             <br/>
             <h2>Demo</h2>
             <br/>
             <p>
                 The purpose of the demo instance is to give anyone a chance to try the features of the application.
                 The data in the demo app is 100 % made up, the loss of it makes no harm to anyone, it is backed up (saved).
                 The data have been set up a way, that covers many use cases, in the point of view of the application.
                 Thus, it is recommended to take a look, before starting any modification in the system (update, delete etc.).
                 Modifications are crucial part of the application. Each and every modification feature is available in the demo application, 
                 to get to know the behaviour of it perfectly.
                 However, these modifications change the initial data, so the bigger the changes, the bigger the difference from the initial demo data. 
                 That's why it is encouraged to create new entities (chemicals, labs, users, etc), test the features of the app with them, 
                 and make modifications on them. 
                 However, the initial demo data is backed up, and periodically being restored. So no worry to mess up something.
                 The only strong request is that please don't corrupt/delete/ruin the data on purpose! 
                 It will only makes the next person annoyed, who wants to try the app. 
             </p>
             <p>
                Keep it in mind, that the data in the demo app is restored in some time (means the previous data is lost forever), so <b>IT CAN NOT BE USED IN PRODUCTION</b>. 
                The operator of the chemstore demo application doesn't bear any responsibility for the data in the application 
                (provided by user or the initial data) and doesn't bear any responsibility for the availability of the demo application.  
             </p>
             <h3>Demo Credentials</h3>
             <p>
                In the demo app, each username ends with <code>@account.com</code>. The default password is the the username before the <code>@</code> symbol. 
                For example you can login as 'Strong Admin' with the following credentials: username:'strongadmin@account.com', password:'strongadmin'</p>
                Or with 'Alpha Lab User' with 'alabuser@account.com' and 'alabuser'.
                There are many users in the system. You can check them under 'Users' point. The username is in the first column. 
                With one exception, each password is the default password (username before @ symbol).
                You can choose between many users, who are assinged to different labs, with different roles. 
                For the different users, different featues are available depends on which role do they have and for which labs. 

             <br/>
             <br/>
            <h2>Roles</h2>
            <br/>

            <h3>User</h3>
            <br/>
            <p>
                A User is typically a technician, whose daily work is to use the chemicals and make mixtures (e.g. eluents) out of it, 
                without additional admin role.
            </p>
            <p>
                A User can see the every laboratory and User in the system (account), but he/she cannot modify these. 
            </p>
            <p>
                A User can be assigned to one or more laboratories (labs) by the Account Manager (see below). He/she can open and consume chemicals (chem items) in these labs.
                A User can see the chemicals and the chemical categories in each assigned lab, but cannot modify these.
            </p>
            <p>
                A User can choose from the dropdown list found in the right header, which laboratory he/she would like to act.
                E.g. in which lab he/she would like to open a certain chemical.
            </p>
            <p>
                A User can change his/her password, and can log out.
            </p>





            <h3>Lab Admin</h3>
            <br/>
            <p>
                The Lab Admin is typically an employee, whose responsibility is to administrate the everyday life of the laboratory in the application. 
                The Lab Admin registers the shipped chemicals, and administrates the related records. 
                A Lab Admin has all the User permissions by default (for the assigned labs), plus some extra detailed below. 
                
            </p>
            
            <p>
                A Lab Admin can be assigned to one or more laboratories (labs) by the Account Manager. 
                If an employee is assigned to a lab as Lab Admin, it is not required to assign him/her as user to the same lab. 
                Besides opening and consuming chemicals (chem items), he/she can add new chem items to the lab.
            </p>
            <p>A Lab Admin can add, modify and delete (archive) chemicals and chemical categories, for the lab he/she administrates.</p>
            <p>A Lab Admin can add, modify and delete (archive) manufacturers throughout the whole system (account).</p>
            
            
            
            <h3>Lab Manager</h3>
            <br/>
            <p>
                The Lab Manager is typically the leader or the deputy leader of the laboratory, or the some kind of responsible person (e.g. quality assurer).
                It is usually the Lab Manager, who is responsible the whole laboratory, especially in the aspect of quality assurance.
                Only the Lab Manager has the authority to perform quality assurance critical modifications (e.g. deleting a chem item). 
                Of course, these modifications are registered to the unmodifiable audit log (see below). 
                
            </p>
            <p>
                A Lab Manager can be assigned to one or more laboratories (labs) by the Account Manager.
                It is advisable to have more lab managers for one laboratory!
                If an employee is assigned to a lab as Lab Manager, it is not required to assign him/her as a Lab Admin or as a user to the same lab. 
                A Lab Manager has all permissions as the Lab Admin by default, plus some extra detailed below. 

            </p>
            <p>The Lab Manager can modify (in progress) and delete chem items from the system (hard delete).</p>
            <p>The Lab Manager can assign employees to the lab as 'users' or 'lab admins' (in progess).</p>


            <h3>Account Manager</h3>
            <br/>
            <p>
                The Account Manager is an employee, who is responsible for the application in a high level. 
                He/she registers laboratories, users etc, but he/she is not typically a User or Admin of the lab. 
                For example, Account Manager can be IT system admin, a higher level leader or quality assurer.

            </p>
            <p>
                Account admin can create, modify and delete labs and users. 
                He/she can assign the employees to the labs as users and/or lab admins. He/she can modify the managers of the labs.
            </p>
            <p>
                He/she can restore the password of the users.
            </p>
            <br/>
            <br/>
            <h2>Entities</h2>
            <br/>
            <h3>User</h3>
            <p>
                User is an employee of the company/account. 
                Users can be added to the system by the Account Manager. 
                The User is created by a default password (the username before the @ symbol).
                The User must change this password at the first login.
                He/she can be assigned to different labs accross the system by an Account Manager or a Lab Manager (in progress). 
                Each operation can be performed only after the login. Each quality critical operation is logged in audit log (in progress).
            </p>
            <br/>
            <h3>Lab</h3>
            <p>
                Lab is a laboratory of the company/account. There can be one or more Lab in the system/account.
                Labs can be added to the system by an Account Manager. One manager of the lab is mandatory. 
                Only the Account Manager can modify the attributes (name, manager) of the labs.
                Lab is one of the most important entity in the application, many entity is linked to it (eg chemical, chem item).
            </p>
            <br/>
            <h3>Manufacturer</h3>
            <p>
                Manufacturers are common throughout the whole company/account. 
                This means a manufacturer that is added for lab1, can be used for lab2.
            </p>
            <br/>
            <h3>(Chemical) Category</h3>
            <p>
                The purpose of (Chemical) Category is to group different chemicals together based on the same shelf life of the chemicals 
                (expiration time, after opening). 
                E.g. ACN and MeOH (chemicals) are both organic solvents (category). 
                We can set an expiration time for organic solvent category (e.g. '90 days after opening'), and it would be applied for each 
                organic solvent chemical (ACN and MeOH). 
                (Chemical) Categories are linked to a specific lab. 
                This means a category that is added for lab1, can't be used for lab2. 
                Of course the very same category can be added for lab2 too, but it must keep it mind that those two are different entites.
                
            </p>
            <br/>
            <h3>Chemical</h3>
            <p>
                A chemical entity represents a certain type (not a concrete bottle) of chemical. 
            </p>
            <br/>
            <h3>Chem Item</h3>
            <p>
                A chem item represents a concrete packaged (bottle or box of) chemical, that can (or could) be found in the laboratory, 
                with an exact manufacturer, batch number, volume etc. 
                One of the main features of the app is the administration of the status of the chem items through the time.
            </p>
            <p>
                A chem item is shipped to the lab, and the Lab Admin or the Lab Manager registers it. 
                The registered chem item can only be used after someone opened it. 
                When opened, an expiration date is calculated based on the defined category (if any) AND the manufacturer expiration date. 
                If no category is provided, the expiration date will be the same as the manufacturer expiration date. 
                If category is provided, the expiration date will be the lesser value between the calculated expiration date and 
                the manufacturer expiration date.
                The calculated expiration date equals the current date (when opened) plus the shelf life given in the related category. 
                When a chem item is consumed, this must be registered in the application too, to prevent the usage of this chemical any longer by mistake. 
                <b> Only opened, not consumed and not expired chem items can be used for the creation of mixtures and eluents!</b>
            </p>
            <p> 
                The chem items in the lab can be listed under the 'Chem items' tab. 
                Automatically, it will show the 'available' chem items, meaning the ones that are not consumed or expired. 
                If the 'Only available' is unchecked, each chem item is viewable. 
                A registered chem item cannot be modified, because many attributes are automatically calculated 
                (e.g. expiration date or who registered it) (This will change in the future. 
                If a chem item registered with bad data, the Lab Manager can delete the incorrect record, and add a new one with the correct data.

            </p>
            <p>
                Lab manager or Lab Admin can register chem items with the following data (each required):
            </p>

            <ul>
                <li><b>chemical:</b> The type of the chem item (pre registered under 'Chemicals')</li>
                <li><b>manufacturer:</b> The manufacturer of the chem item (pre registered under 'Manufacturers')</li>
                <li><b>batch number:</b> The batch number (or lot number) of the chem item (free text)</li>
                <li><b>quantity:</b> The quantity of packaging, volume or mass (number)</li>
                <li><b>unit:</b> The unit of the quantity (one from the fixed list)</li>
                <li><b>amount:</b> How many bottles/boxes are to register (number)</li>
                <li><b>Expiration date (before opened):</b> The expiration date, given by manufacturer, without opening</li>
                <li><b>Arrival date: </b>When the chem item is arived/registered in the lab</li>
            </ul>

        </div>
        
        
    )
}

export default UserManual